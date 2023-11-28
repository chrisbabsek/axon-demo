package de.babsek.demo.axontesting.domain.eventhandler

import de.babsek.demo.axontesting.configuration.ProcessingGroups
import mu.KotlinLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@ProcessingGroup(ProcessingGroups.SUBSCRIBING)
@Component
class LoggingEventHandler {

    @EventHandler
    fun on(event: Any) {
        logger.info { event }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
