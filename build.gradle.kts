plugins {
    id("com.diffplug.spotless") version "8.11.0"

    // Versjonene for tjenestenes plugins settes her, én gang for alle.
    // `apply false` betyr at roten selv ikke bruker dem; tjenestene tar dem i bruk uten versjon.
    kotlin("jvm") version "2.3.21" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
    kotlin("plugin.jpa") version "2.3.21" apply false
    id("org.springframework.boot") version "4.1.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

val ktlintVersion = "1.8.0"

allprojects {
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    spotless {
        kotlin {
            target("src/**/*.kt")
            ktlint(ktlintVersion)
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(ktlintVersion)
        }
    }
}
