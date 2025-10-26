package com.docchain.user.infrastructure.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Event Consumer para monitorar eventos do Circuit Breaker.
 * 
 * PROPÓSITO:
 * - Alertas: Notifica quando o circuito muda de estado (CRITICAL!)
 * - Diagnóstico: Ajuda a identificar quando serviços estão degradados
 * - Métricas: Base para dashboards e alertas de produção
 * 
 * COMO FUNCIONA:
 * 1. Registrado automaticamente pelo Resilience4j quando o Bean é criado
 * 2. Escuta quando uma instância de CircuitBreaker é adicionada ao registry
 * 3. Adiciona listeners para eventos críticos do circuito
 * 4. Loga com níveis adequados (WARN para mudanças de estado, ERROR para rejeições)
 * 
 * EVENTOS MONITORADOS:
 * - onStateTransition: Mudança de estado CLOSED ↔ OPEN ↔ HALF-OPEN (log WARN com emoji)
 * - onSuccess: Requisição bem-sucedida (log DEBUG, apenas para troubleshooting)
 * - onError: Requisição com erro (log ERROR)
 * - onCallNotPermitted: Requisição bloqueada (circuito OPEN) (log ERROR com emoji)
 * 
 * ESTADOS DO CIRCUIT BREAKER:
 * - CLOSED: Normal, requisições passam
 * - OPEN: Bloqueado, requisições rejeitadas imediatamente
 * - HALF-OPEN: Testando recuperação, permite algumas requisições
 * - DISABLED: Desabilitado (não usado normalmente)
 * - FORCED_OPEN: Forçado aberto manualmente
 * - METRICS_ONLY: Apenas coletando métricas
 */
@Configuration
@Slf4j
public class CircuitBreakerEventConsumer {

    @Bean
    public RegistryEventConsumer<CircuitBreaker> customCircuitBreakerEventConsumer() {
        return new RegistryEventConsumer<CircuitBreaker>() {
            
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                CircuitBreaker circuitBreaker = entryAddedEvent.getAddedEntry();
                log.info("[CIRCUIT-BREAKER] '{}' registered in registry with state: {}", 
                    circuitBreaker.getName(),
                    circuitBreaker.getState());

                circuitBreaker.getEventPublisher()
                    .onStateTransition(event -> {
                        String stateLabel = switch (event.getStateTransition().getToState()) {
                            case CLOSED -> "[CLOSED]";
                            case OPEN -> "[OPEN]";
                            case HALF_OPEN -> "[HALF-OPEN]";
                            case DISABLED -> "[DISABLED]";
                            case FORCED_OPEN -> "[FORCED-OPEN]";
                            case METRICS_ONLY -> "[METRICS-ONLY]";
                        };
                        
                        log.warn("[CIRCUIT-BREAKER] {} '{}' state transition: {} -> {} at {}", 
                            stateLabel,
                            event.getCircuitBreakerName(),
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState(),
                            event.getCreationTime());
                    })

                    .onSuccess(event -> 
                        log.debug("[CIRCUIT-BREAKER-SUCCESS] '{}' recorded success. Duration: {}ms", 
                            event.getCircuitBreakerName(),
                            event.getElapsedDuration().toMillis()))

                    .onError(event -> 
                        log.error("[CIRCUIT-BREAKER-ERROR] '{}' recorded error: {} - Duration: {}ms", 
                            event.getCircuitBreakerName(),
                            event.getThrowable().getMessage(),
                            event.getElapsedDuration().toMillis()))

                    .onCallNotPermitted(event -> 
                        log.error("[CIRCUIT-BREAKER-REJECTED] '{}' call rejected - Circuit is {} at {}", 
                            event.getCircuitBreakerName(),
                            circuitBreaker.getState(),
                            event.getCreationTime()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                log.info("CircuitBreaker '{}' removed from registry", 
                    entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                log.info("CircuitBreaker '{}' replaced in registry", 
                    entryReplacedEvent.getNewEntry().getName());
            }
        };
    }
}
