package no.valg.opptelling

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class UgyldigResultatException(
    melding: String,
) : RuntimeException(melding)

@Service
class OpptellingService(
    private val behandletRepo: BehandletMeldingRepository,
    private val stemmerRepo: PartiStemmerRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun registrer(melding: ValgresultatMelding) {
        // 1. Idempotens: har vi sett denne meldingen før?
        if (behandletRepo.existsById(melding.meldingId)) {
            log.warn("Duplikat ignorert: {}", melding.meldingId)
            return
        }

        // 2. Validering: kaster unntak → transaksjonen rulles tilbake → meldingen går til DLQ
        valider(melding)

        // 3. Foreløpig resultat skal ikke overskrive et endelig resultat
        val eksisterende =
            stemmerRepo.findByValgTypeAndFylkeAndKommuneAndKrets(
                melding.valgType,
                melding.fylke,
                melding.kommune,
                melding.krets,
            )
        if (!melding.erEndelig && eksisterende.any { it.erEndelig }) {
            log.info("Ignorerer foreløpig resultat for {} – endelig resultat finnes allerede", melding.krets)
            behandletRepo.save(BehandletMelding(melding.meldingId))
            return
        }

        // 4. Erstatt kretsens tidligere tall med de nye
        stemmerRepo.deleteAll(eksisterende)
        stemmerRepo.saveAll(
            melding.stemmerPerParti.map { (parti, antall) ->
                PartiStemmer(
                    valgType = melding.valgType,
                    fylke = melding.fylke,
                    kommune = melding.kommune,
                    krets = melding.krets,
                    parti = parti,
                    stemmer = antall,
                    erEndelig = melding.erEndelig,
                    meldingId = melding.meldingId,
                )
            },
        )

        // 5. Marker som behandlet – i SAMME transaksjon som stemmetallene
        behandletRepo.save(BehandletMelding(melding.meldingId))

        log.info(
            "Registrerte {} for {}/{} ({} partier, endelig={})",
            melding.meldingId,
            melding.kommune,
            melding.krets,
            melding.stemmerPerParti.size,
            melding.erEndelig,
        )
    }

    private fun valider(melding: ValgresultatMelding) {
        if (melding.stemmerPerParti.isEmpty()) {
            throw UgyldigResultatException("Ingen stemmetall i ${melding.meldingId}")
        }
        val negative = melding.stemmerPerParti.filterValues { it < 0 }
        if (negative.isNotEmpty()) {
            throw UgyldigResultatException("Negative stemmetall i ${melding.meldingId}: $negative")
        }
    }
}
