package no.valg.revisjon

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        const val RESULTAT_EXCHANGE = "valg.resultater.exchange"
        const val DLX = "valg.dlx"
        const val DLQ = "valg.dlq"
        const val QUEUE = "valg.revisjon.queue"
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
    fun revisjonQueue(): Queue = QueueBuilder.durable(QUEUE).deadLetterExchange(DLX).build()

    @Bean
    fun revisjonBinding(): Binding = BindingBuilder.bind(revisjonQueue()).to(resultatExchange()).with("resultat.#")
}
