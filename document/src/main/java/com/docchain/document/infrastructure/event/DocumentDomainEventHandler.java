package com.docchain.document.infrastructure.event;

import com.docchain.document.domain.event.DocumentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentDomainEventHandler {

    @EventListener
    public void handle(DocumentCreatedEvent event) {
        log.info(event.toString());
    }
}
