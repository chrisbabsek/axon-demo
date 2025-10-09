package de.babsek.demo.axontesting.domain.eventhandler

import com.opencqrs.framework.eventhandler.EventHandling
import de.babsek.demo.axontesting.configuration.ProcessingGroups
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class LoggingEventHandler {

    @EventHandling(ProcessingGroups.SUBSCRIBING)
    fun on(event: Any) {
        logger.info { event }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
