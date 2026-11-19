package no.valg.fylkesoversikt

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class Fylkesoversikt {
    private val log = LoggerFactory.getLogger(javaClass)

    /** Nøkkel: "VALGTYPE/kommune/krets" → siste mottatte resultat for kretsen. */
    private val sisteResultatPerKrets = ConcurrentHashMap<String, ValgresultatMelding>()

    fun oppdater(melding: ValgresultatMelding) {
        val nokkel = "${melding.valgType}/${melding.kommune}/${melding.krets}"
        sisteResultatPerKrets[nokkel] = melding

        if (melding.erEndelig) {
            log.info(
                "📰 VARSEL TIL LOKALAVISENE: Endelig {}-resultat fra {} krets i {}: {}",
                melding.valgType.lowercase(),
                melding.krets,
                melding.kommune,
                melding.stemmerPerParti,
            )
        } else {
            log.info("Foreløpig resultat fra {} / {}", melding.kommune, melding.krets)
        }
    }

    /** Summerer stemmer per parti, gruppert på valgtype. */
    fun oversikt(): Map<String, Map<String, Int>> =
        sisteResultatPerKrets.values
            .groupBy { it.valgType }
            .mapValues { (_, meldinger) ->
                meldinger
                    .flatMap { it.stemmerPerParti.entries }
                    .groupBy({ it.key }, { it.value })
                    .mapValues { (_, stemmer) -> stemmer.sum() }
            }
}
