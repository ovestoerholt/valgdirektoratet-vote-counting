package no.valg.innrapportering

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

/**
 * Et opptalt resultat fra én valgkrets.
 *
 * meldingId settes av avsender (kommunens system). Hvis avsender sender samme
 * resultat på nytt (f.eks. etter nettverksfeil), skal meldingId være den samme,
 * slik at mottakerne kan oppdage duplikatet.
 */
data class ValgresultatMelding(
    @field:NotBlank val meldingId: String,
    @field:NotBlank val valgType: String, // "STORTINGSVALG" eller "KOMMUNEVALG"
    @field:NotBlank val fylke: String, // "Vestfold"
    @field:NotBlank val kommune: String, // "Sandefjord"
    @field:NotBlank val krets: String, // "Stokke"
    @field:NotEmpty val stemmerPerParti: Map<String, Int>,
    val erEndelig: Boolean = false, // foreløpig telling vs. endelig godkjent
)
