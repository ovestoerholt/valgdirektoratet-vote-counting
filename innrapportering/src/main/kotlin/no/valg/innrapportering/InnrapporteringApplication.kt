package no.valg.innrapportering

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class InnrapporteringApplication

fun main(args: Array<String>) {
    runApplication<InnrapporteringApplication>(*args)
}

@RestController
class RootController {
    @GetMapping("/")
    fun root() = "Innrapportering svarer"
}
