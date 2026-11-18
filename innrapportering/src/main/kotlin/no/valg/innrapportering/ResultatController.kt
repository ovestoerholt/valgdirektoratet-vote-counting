package no.valg.innrapportering

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resultater")
class ResultatController(
    private val publisher: ResultatPublisher,
) {
    @PostMapping
    fun motta(
        @Valid @RequestBody melding: ValgresultatMelding,
    ): ResponseEntity<Map<String, String>> {
        val routingKey = publisher.publiser(melding)
        return ResponseEntity.accepted().body(
            mapOf("meldingId" to melding.meldingId, "routingKey" to routingKey),
        )
    }
}
