package com.docchain.user.infrastructure.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
