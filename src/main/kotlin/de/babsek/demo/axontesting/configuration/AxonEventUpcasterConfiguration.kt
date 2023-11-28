package de.babsek.demo.axontesting.configuration

import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonEventUpcasterConfiguration {

    @Bean
    fun eventUpcasterChain() = EventUpcasterChain()
}
