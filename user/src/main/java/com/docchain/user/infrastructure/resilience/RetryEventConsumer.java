package com.docchain.user.infrastructure.resilience;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RetryEventConsumer {

    @Bean
    public RegistryEventConsumer<Retry> customRetryEventConsumer() {
        return new RegistryEventConsumer<Retry>() {
            
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                Retry retry = entryAddedEvent.getAddedEntry();
                log.info("[RETRY] '{}' registered in registry", retry.getName());

                retry.getEventPublisher()
                    .onRetry(event -> 
                        log.warn("[RETRY] Attempt {}/{} for '{}': {} - Last error: {}", 
                            event.getNumberOfRetryAttempts(),
                            retry.getRetryConfig().getMaxAttempts(),
                            event.getName(),
                            event.getCreationTime(),
                            event.getLastThrowable().getMessage()))

                    .onSuccess(event -> 
                        log.info("[RETRY-SUCCESS] '{}' succeeded after {} attempt(s)", 
                            event.getName(),
                            event.getNumberOfRetryAttempts()))

                    .onError(event -> 
                        log.error("[RETRY-FAILED] '{}' failed after {} attempt(s). Final error: {} - {}", 
                            event.getName(),
                            event.getNumberOfRetryAttempts(),
                            event.getLastThrowable().getClass().getSimpleName(),
                            event.getLastThrowable().getMessage()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {
                log.info("Retry '{}' removed from registry", 
                    entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {
                log.info("Retry '{}' replaced in registry", 
                    entryReplacedEvent.getNewEntry().getName());
            }
        };
    }
}
