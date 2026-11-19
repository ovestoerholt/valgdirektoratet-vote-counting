package no.valg.opptelling

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NasjonalOpptellingListener(
    private val opptellingService: OpptellingService,
) {
    @RabbitListener(queues = [RabbitConfig.QUEUE])
    fun mottaResultat(melding: ValgresultatMelding) {
        opptellingService.registrer(melding)
    }
}
