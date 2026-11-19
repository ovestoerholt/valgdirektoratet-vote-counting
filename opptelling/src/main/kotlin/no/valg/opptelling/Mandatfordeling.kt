package no.valg.opptelling

object Mandatfordeling {
    const val STORTINGETS_MANDATER = 169

    fun fordel(
        stemmer: Map<String, Long>,
        antallMandater: Int = STORTINGETS_MANDATER,
        forsteDivisor: Double = 1.4,
    ): Map<String, Int> {
        require(antallMandater >= 0) { "antallMandater kan ikke være negativt" }

        val mandater = stemmer.keys.associateWith { 0 }.toMutableMap()
        if (stemmer.isEmpty() || stemmer.values.sum() == 0L) return mandater

        repeat(antallMandater) {
            val vinner =
                stemmer
                    .maxBy { (parti, antall) ->
                        kvotient(antall, mandater.getValue(parti), forsteDivisor)
                    }.key
            mandater[vinner] = mandater.getValue(vinner) + 1
        }
        return mandater
    }

    private fun kvotient(
        stemmer: Long,
        mandater: Int,
        forsteDivisor: Double,
    ): Double {
        val divisor = if (mandater == 0) forsteDivisor else 2.0 * mandater + 1
        return stemmer / divisor
    }
}
