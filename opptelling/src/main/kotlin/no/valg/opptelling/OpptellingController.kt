package no.valg.opptelling

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class NasjonaltResultat(
    val valgType: String,
    val totaltAntallStemmer: Long,
    val stemmerPerParti: Map<String, Long>,
    val mandater: Map<String, Int>,
)

@RestController
@RequestMapping("/api/opptelling")
class OpptellingController(
    private val stemmerRepo: PartiStemmerRepository,
) {
    @GetMapping("/nasjonal")
    fun nasjonal(): NasjonaltResultat {
        val stemmer =
            stemmerRepo
                .summerPerParti("STORTINGSVALG")
                .associate { it.parti to it.stemmer }

        return NasjonaltResultat(
            valgType = "STORTINGSVALG",
            totaltAntallStemmer = stemmer.values.sum(),
            stemmerPerParti = stemmer,
            mandater = Mandatfordeling.fordel(stemmer),
        )
    }
}
