package valgdirektoratet.votecounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class VotecountingApplication

fun main(args: Array<String>) {
	runApplication<VotecountingApplication>(*args)
}


@RestController
class HelloController {
        @GetMapping("/")
        fun hello() = "Vote counting is up"
}
