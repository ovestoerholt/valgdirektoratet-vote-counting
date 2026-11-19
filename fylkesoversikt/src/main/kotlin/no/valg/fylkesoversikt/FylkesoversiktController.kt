package no.valg.fylkesoversikt

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FylkesoversiktController(
    private val fylkesoversikt: Fylkesoversikt,
) {
    @GetMapping("/api/fylkesoversikt")
    fun oversikt() = fylkesoversikt.oversikt()
}
