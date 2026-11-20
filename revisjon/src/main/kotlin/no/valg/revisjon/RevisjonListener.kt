package no.valg.revisjon

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Component

@Component
class RevisjonListener(
    private val jdbcClient: JdbcClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** Tar imot rå AMQP-melding (ingen JSON-konvertering), slik at vi lagrer nøyaktig det som kom. */
    @RabbitListener(queues = [RabbitConfig.QUEUE])
    fun motta(message: Message) {
        val props = message.messageProperties
        val payload = String(message.body, Charsets.UTF_8)

        jdbcClient
            .sql(
                """
            INSERT INTO revisjonslogg (melding_id, routing_key, payload)
            VALUES (:meldingId, :routingKey, :payload)
            """,
            ).param("meldingId", props.messageId)
            .param("routingKey", props.receivedRoutingKey)
            .param("payload", payload)
            .update()

        log.info("Revisjonslogget {} ({})", props.messageId, props.receivedRoutingKey)
    }
}
