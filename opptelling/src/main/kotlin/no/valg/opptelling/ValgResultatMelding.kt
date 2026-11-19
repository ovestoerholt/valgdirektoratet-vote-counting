package no.valg.opptelling

data class ValgresultatMelding(
    val meldingId: String,
    val valgType: String,
    val fylke: String,
    val kommune: String,
    val krets: String,
    val stemmerPerParti: Map<String, Int>,
    val erEndelig: Boolean = false,
)
