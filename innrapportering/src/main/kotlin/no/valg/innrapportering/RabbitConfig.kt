package no.valg.innrapportering

import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        const val RESULTAT_EXCHANGE = "valg.resultater.exchange"
    }

    /** Spring Boot deklarerer denne i RabbitMQ automatisk (durable = true som standard). */
    @Bean
    fun resultatExchange(): TopicExchange = TopicExchange(RESULTAT_EXCHANGE)

    /** Send meldinger som JSON i stedet for serialiserte Java-objekter. */
    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()
}
