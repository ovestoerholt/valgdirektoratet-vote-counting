package no.valg.revisjon

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

data class Revisjonspost(
    val id: Long,
    val meldingId: String?,
    val routingKey: String,
    val payload: String,
    val mottatt: OffsetDateTime,
)

@RestController
class RevisjonController(
    private val jdbcClient: JdbcClient,
) {
    @GetMapping("/api/revisjon")
    fun siste(
        @RequestParam(defaultValue = "20") antall: Int,
    ): List<Revisjonspost> =
        jdbcClient
            .sql(
                """
            SELECT id, melding_id, routing_key, payload, mottatt
            FROM revisjonslogg
            ORDER BY id DESC
            LIMIT :antall
            """,
            ).param("antall", antall)
            .query(Revisjonspost::class.java) // kolonne melding_id → parameter meldingId
            .list()
            .filterNotNull()
}
