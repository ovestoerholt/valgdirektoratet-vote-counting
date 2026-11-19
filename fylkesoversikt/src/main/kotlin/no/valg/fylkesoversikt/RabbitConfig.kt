package no.valg.fylkesoversikt

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig(
    @Value("\${fylkesoversikt.fylke}") private val fylke: String,
) {
    companion object {
        const val RESULTAT_EXCHANGE = "valg.resultater.exchange"
        const val DLX = "valg.dlx"
        const val DLQ = "valg.dlq"
    }

    @Bean
    fun resultatExchange() = TopicExchange(RESULTAT_EXCHANGE)

    @Bean
    fun deadLetterExchange() = FanoutExchange(DLX)

    @Bean
    fun deadLetterQueue(): Queue = QueueBuilder.durable(DLQ).build()

    @Bean
    fun deadLetterBinding(): Binding = BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange())

    @Bean
    fun fylkeQueue(): Queue =
        QueueBuilder
            .durable("valg.$fylke.queue")
            .deadLetterExchange(DLX)
            .build()

    // resultat.<hvilken som helst valgtype>.<fylke>.<hvilken som helst kommune>
    @Bean
    fun fylkeBinding(): Binding = BindingBuilder.bind(fylkeQueue()).to(resultatExchange()).with("resultat.*.$fylke.*")

    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()
}
