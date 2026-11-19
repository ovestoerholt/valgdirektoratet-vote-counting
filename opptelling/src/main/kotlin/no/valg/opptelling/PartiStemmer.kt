package no.valg.opptelling

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "parti_stemmer")
class PartiStemmer(
    val valgType: String,
    val fylke: String,
    val kommune: String,
    val krets: String,
    val parti: String,
    val stemmer: Int,
    val erEndelig: Boolean,
    val meldingId: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)