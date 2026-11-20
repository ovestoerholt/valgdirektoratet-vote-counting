package no.valg.innrapportering

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
@ConditionalOnProperty(prefix = "simulator", name = ["enabled"], havingValue = "true")
class ValgnattSimulator(
    private val publisher: ResultatPublisher,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private data class Krets(
        val fylke: String,
        val kommune: String,
        val navn: String,
    )

    private val kretser =
        listOf(
            Krets("Vestfold", "Sandefjord", "Stokke"),
            Krets("Vestfold", "Sandefjord", "Sentrum"),
            Krets("Vestfold", "Sandefjord", "Andebu"),
            Krets("Vestfold", "Tønsberg", "Nøtterøy"),
            Krets("Vestfold", "Larvik", "Stavern"),
            Krets("Oslo", "Oslo", "Grünerløkka"),
            Krets("Oslo", "Oslo", "Frogner"),
            Krets("Vestland", "Bergen", "Fana"),
            Krets("Trøndelag", "Trondheim", "Byåsen"),
            Krets("Troms", "Tromsø", "Kvaløya"),
        )

    private val partier = listOf("AP", "H", "FRP", "SV", "SP", "KRF", "V", "MDG", "R")

    private var forrige: ValgresultatMelding? = null

    @Scheduled(fixedRateString = "\${simulator.intervall-ms:1000}")
    fun sendResultat() {
        val terning = Random.nextInt(100)
        val melding =
            when {
                terning < 5 && forrige != null -> {
                    forrige!!.also { log.info("🎲 Sender duplikat") }
                }

                terning < 8 -> {
                    lagMelding()
                        .let { it.copy(stemmerPerParti = it.stemmerPerParti + ("AP" to -42)) }
                        .also { log.info("🎲 Sender korrupt melding") }
                }

                else -> {
                    lagMelding()
                }
            }
        forrige = melding
        publisher.publiser(melding)
    }

    private fun lagMelding(): ValgresultatMelding {
        val krets = kretser.random()
        return ValgresultatMelding(
            meldingId = UUID.randomUUID().toString(),
            valgType = if (Random.nextInt(5) == 0) "KOMMUNEVALG" else "STORTINGSVALG",
            fylke = krets.fylke,
            kommune = krets.kommune,
            krets = krets.navn,
            stemmerPerParti = partier.associateWith { Random.nextInt(0, 2000) },
            erEndelig = Random.nextInt(4) == 0,
        )
    }
}
