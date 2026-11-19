package no.valg.opptelling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpptellingApplication

fun main(args: Array<String>) {
    runApplication<OpptellingApplication>(*args)
}
