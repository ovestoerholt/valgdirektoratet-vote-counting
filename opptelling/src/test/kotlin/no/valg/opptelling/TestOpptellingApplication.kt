package no.valg.opptelling

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<OpptellingApplication>().with(TestcontainersConfiguration::class).run(*args)
}
