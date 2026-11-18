package valgdirektoratet.votecounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VotecountingApplication

fun main(args: Array<String>) {
	runApplication<VotecountingApplication>(*args)
}
