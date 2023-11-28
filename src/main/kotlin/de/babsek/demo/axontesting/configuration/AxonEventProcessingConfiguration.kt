package de.babsek.demo.axontesting.configuration

import mu.KotlinLogging
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.ErrorHandler
import org.axonframework.eventhandling.ListenerInvocationErrorHandler
import org.axonframework.eventhandling.PropagatingErrorHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class AxonEventProcessingConfiguration {

    @Autowired
    fun configureErrorPropagation(config: EventProcessingConfigurer) {
        config.registerDefaultErrorHandler {
            ErrorHandler { ctx ->
                if (ctx.error() is Error) {
                    logger.error(ctx.error()) {
                        "Releasing claim on token potentially, due to failed event processor: ${ctx.eventProcessor()}"
                    }
                }
                PropagatingErrorHandler.INSTANCE.handleError(ctx)
            }
        }
        config.registerDefaultListenerInvocationErrorHandler {
            ListenerInvocationErrorHandler { exception, event, eventHandler ->
                logger.warn(exception) {
                    "Releasing claim on token potentially, due to failed event handler [${eventHandler.targetType.simpleName}] failed to handle event [${event.identifier}] (${event.payloadType.name}): ${event.payload}"
                }
                PropagatingErrorHandler.INSTANCE.onError(exception, event, eventHandler)
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
