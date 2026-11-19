package no.valg.opptelling

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    companion object {
        const val RESULTAT_EXCHANGE = "valg.resultater.exchange"
        const val DLX = "valg.dlx"
        const val DLQ = "valg.dlq"
        const val QUEUE = "valg.nasjonal.queue"
        const val BINDING = "resultat.stortingsvalg.#"
    }

    // Samme exchange som produsenten. Å deklarere den på nytt er trygt (idempotent),
    // og gjør at konsumenten kan starte før produsenten.
    @Bean
    fun resultatExchange() = TopicExchange(RESULTAT_EXCHANGE)

    // --- Dead letter-oppsett ---
    @Bean
    fun deadLetterExchange() = FanoutExchange(DLX)

    @Bean
    fun deadLetterQueue(): Queue = QueueBuilder.durable(DLQ).build()

    @Bean
    fun deadLetterBinding(): Binding = BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange())

    // --- Hovedkøen ---
    @Bean
    fun nasjonalQueue(): Queue =
        QueueBuilder.durable(QUEUE)
            .deadLetterExchange(DLX)   // x-dead-letter-exchange
            .maxPriority(10)           // x-max-priority (brukes i steg 13)
            .build()

    @Bean
    fun nasjonalBinding(): Binding =
        BindingBuilder.bind(nasjonalQueue()).to(resultatExchange()).with(BINDING)

    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()
}