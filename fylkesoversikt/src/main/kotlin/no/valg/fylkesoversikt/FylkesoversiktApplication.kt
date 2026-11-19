package no.valg.fylkesoversikt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FylkesoversiktApplication

fun main(args: Array<String>) {
    runApplication<FylkesoversiktApplication>(*args)
}
