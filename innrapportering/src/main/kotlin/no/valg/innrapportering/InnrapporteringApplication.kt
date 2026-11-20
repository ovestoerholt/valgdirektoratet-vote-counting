package no.valg.innrapportering

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InnrapporteringApplication

fun main(args: Array<String>) {
    runApplication<InnrapporteringApplication>(*args)
}
