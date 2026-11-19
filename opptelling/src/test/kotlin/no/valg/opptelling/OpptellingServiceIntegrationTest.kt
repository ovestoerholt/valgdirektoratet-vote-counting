package no.valg.opptelling

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class OpptellingServiceIntegrationTest(
    @Autowired private val service: OpptellingService,
    @Autowired private val stemmerRepo: PartiStemmerRepository,
) {
    private fun melding(
        meldingId: String,
        krets: String,
        stemmer: Map<String, Int>,
        erEndelig: Boolean = false,
    ) = ValgresultatMelding(meldingId, "STORTINGSVALG", "Vestfold", "Sandefjord", krets, stemmer, erEndelig)

    private fun stemmerI(krets: String) =
        stemmerRepo
            .findByValgTypeAndFylkeAndKommuneAndKrets("STORTINGSVALG", "Vestfold", "Sandefjord", krets)
            .associate { it.parti to it.stemmer }

    @Test
    fun `samme melding to ganger telles bare én gang`() {
        val m = melding("it-dup-1", "Krets-A", mapOf("AP" to 100, "H" to 50))
        service.registrer(m)
        service.registrer(m)

        assertEquals(mapOf("AP" to 100, "H" to 50), stemmerI("Krets-A"))
    }

    @Test
    fun `nye tall for samme krets erstatter de gamle`() {
        service.registrer(melding("it-oppd-1", "Krets-B", mapOf("AP" to 100)))
        service.registrer(melding("it-oppd-2", "Krets-B", mapOf("AP" to 120, "H" to 30)))

        assertEquals(mapOf("AP" to 120, "H" to 30), stemmerI("Krets-B"))
    }

    @Test
    fun `foreløpig resultat overskriver ikke endelig`() {
        service.registrer(melding("it-end-1", "Krets-C", mapOf("AP" to 200), erEndelig = true))
        service.registrer(melding("it-end-2", "Krets-C", mapOf("AP" to 999), erEndelig = false))

        assertEquals(mapOf("AP" to 200), stemmerI("Krets-C"))
    }

    @Test
    fun `negative stemmetall avvises og lagrer ingenting`() {
        assertThrows<UgyldigResultatException> {
            service.registrer(melding("it-neg-1", "Krets-D", mapOf("AP" to -1)))
        }
        assertEquals(emptyMap(), stemmerI("Krets-D"))
    }
}
