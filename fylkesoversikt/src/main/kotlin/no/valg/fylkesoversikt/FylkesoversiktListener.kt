package no.valg.fylkesoversikt

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class FylkesoversiktListener(
    private val fylkesoversikt: Fylkesoversikt,
) {
    // SpEL: slå opp bønnen "fylkeQueue" og bruk navnet dens
    @RabbitListener(queues = ["#{fylkeQueue.name}"])
    fun motta(melding: ValgresultatMelding) {
        fylkesoversikt.oppdater(melding)
    }
}
