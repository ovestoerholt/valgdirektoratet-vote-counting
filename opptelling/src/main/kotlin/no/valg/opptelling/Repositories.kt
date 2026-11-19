package no.valg.opptelling

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BehandletMeldingRepository : JpaRepository<BehandletMelding, String>

interface PartiStemmerRepository : JpaRepository<PartiStemmer, Long> {
    fun findByValgTypeAndFylkeAndKommuneAndKrets(
        valgType: String,
        fylke: String,
        kommune: String,
        krets: String,
    ): List<PartiStemmer>

    @Query(
        """
        SELECT p.parti AS parti, SUM(p.stemmer) AS stemmer
        FROM PartiStemmer p
        WHERE p.valgType = :valgType
        GROUP BY p.parti
        ORDER BY SUM(p.stemmer) DESC
        """,
    )
    fun summerPerParti(
        @Param("valgType") valgType: String,
    ): List<PartiSum>
}

/** Projeksjon: Spring Data lager en implementasjon av dette grensesnittet for hver rad. */
interface PartiSum {
    val parti: String
    val stemmer: Long
}
