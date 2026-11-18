package no.valg.innrapportering

/**
 * Bygger routing key på formen resultat.<valgtype>.<fylke>.<kommune>,
 * f.eks. "resultat.stortingsvalg.vestfold.sandefjord".
 */
fun routingKeyFor(melding: ValgresultatMelding): String =
    listOf("resultat", melding.valgType, melding.fylke, melding.kommune)
        .joinToString(separator = ".") { normaliser(it) }

/** Små bokstaver, og ingen punktum/mellomrom inne i et ord (punktum er ordskille i routing keys). */
private fun normaliser(del: String): String = del.trim().lowercase().replace(Regex("[\\s.]+"), "-")
