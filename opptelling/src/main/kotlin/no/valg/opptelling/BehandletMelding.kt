package no.valg.opptelling

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "behandlet_melding")
class BehandletMelding(
    @Id
    val meldingId: String,
    val behandletTidspunkt: Instant = Instant.now(),
)
