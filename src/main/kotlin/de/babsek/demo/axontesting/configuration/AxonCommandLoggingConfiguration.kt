package de.babsek.demo.axontesting.configuration

import mu.KotlinLogging
import org.axonframework.commandhandling.CommandBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration


@Configuration
class AxonCommandLoggingConfiguration {

    @Autowired
    fun configureExceptionInterceptor(commandBus: CommandBus) {
        commandBus.registerHandlerInterceptor { unitOfWork, interceptorChain ->
            logger.info { "Executing command: ${unitOfWork.message.payload}" }
            interceptorChain.proceed()
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
