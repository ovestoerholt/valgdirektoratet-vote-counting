package no.valg.innrapportering

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class ResultatPublisher(
    private val rabbitTemplate: RabbitTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publiser(melding: ValgresultatMelding): String {
        val routingKey = routingKeyFor(melding)

        rabbitTemplate.convertAndSend(
            RabbitConfig.RESULTAT_EXCHANGE,
            routingKey,
            melding,
            MessagePostProcessor { amqpMelding ->
                amqpMelding.messageProperties.messageId = melding.meldingId
                // Brukes av prioritetskøen i steg 13. Høyere tall = høyere prioritet.
                amqpMelding.messageProperties.priority = if (melding.erEndelig) 10 else 1
                amqpMelding
            },
        )

        log.info("Publiserte {} med routing key {}", melding.meldingId, routingKey)
        return routingKey
    }
}
