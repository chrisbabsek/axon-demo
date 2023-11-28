package de.babsek.demo.axontesting.domain.eventhandler

import mu.KotlinLogging
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class LoggingEventHandler {

    @EventHandler
    fun on(event: Any) {
        logger.info { event }
    }

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
