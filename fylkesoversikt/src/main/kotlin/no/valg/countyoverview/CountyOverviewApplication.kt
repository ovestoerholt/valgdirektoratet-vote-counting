package no.valg.countyoverview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class CountyOverviewApplication

fun main(args: Array<String>) {
    runApplication<CountyOverviewApplication>(*args)
}

@RestController
class RootController {
    @GetMapping("/")
    fun root() = "Fylkesoversikt svarer"
}
