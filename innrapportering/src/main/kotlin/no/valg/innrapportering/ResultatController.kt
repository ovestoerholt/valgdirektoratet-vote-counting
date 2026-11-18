package no.valg.innrapportering

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resultater")
class ResultatController {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun motta(
        @Valid @RequestBody melding: ValgresultatMelding,
    ): ResponseEntity<Map<String, String>> {
        log.info("Mottok {}", melding)
        return ResponseEntity.accepted().body(mapOf("meldingId" to melding.meldingId))
    }
}
