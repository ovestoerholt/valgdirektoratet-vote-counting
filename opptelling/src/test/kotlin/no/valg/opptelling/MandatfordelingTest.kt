package no.valg.opptelling

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MandatfordelingTest {
    @Test
    fun `fordeler mandater etter modifisert Sainte-Laguë`() {
        // Kvotienter: A 714, B 500, C 214 → A, B, A(333), B(233), C(214)
        val resultat =
            Mandatfordeling.fordel(
                mapOf("A" to 1000L, "B" to 700L, "C" to 300L),
                antallMandater = 5,
            )
        assertEquals(mapOf("A" to 2, "B" to 2, "C" to 1), resultat)
    }

    @Test
    fun `summen av mandater er alltid lik antall mandater`() {
        val resultat =
            Mandatfordeling.fordel(
                mapOf("AP" to 520_000L, "H" to 430_000L, "FRP" to 380_000L, "SV" to 180_000L, "V" to 70_000L),
            )
        assertEquals(169, resultat.values.sum())
    }

    @Test
    fun `ingen stemmer gir ingen mandater`() {
        val resultat = Mandatfordeling.fordel(mapOf("A" to 0L, "B" to 0L))
        assertEquals(mapOf("A" to 0, "B" to 0), resultat)
    }
}
