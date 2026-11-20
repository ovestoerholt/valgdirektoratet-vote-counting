# ---------- Byggesteg: kompiler med full JDK ----------
FROM eclipse-temurin:25-jdk AS build
# Hvilken tjeneste som bygges, f.eks. innrapportering (settes i docker-compose.yml)
ARG TJENESTE
WORKDIR /app

# Kopier først bare byggefilene, slik at avhengigheter caches i et eget lag.
# Gradle krever at alle delprosjektene i settings.gradle.kts finnes,
# derfor kopieres byggescriptene til alle fire tjenestene.
COPY gradlew *.gradle.kts ./
COPY gradle gradle
COPY innrapportering/build.gradle.kts innrapportering/
COPY opptelling/build.gradle.kts opptelling/
COPY fylkesoversikt/build.gradle.kts fylkesoversikt/
COPY revisjon/build.gradle.kts revisjon/
RUN chmod +x gradlew && ./gradlew :${TJENESTE}:dependencies --no-daemon > /dev/null

# Kopier kildekoden til denne tjenesten og bygg (testene kjøres lokalt, ikke i Docker-bygget)
COPY ${TJENESTE}/src ${TJENESTE}/src
RUN ./gradlew :${TJENESTE}:bootJar --no-daemon -x test

# ---------- Kjøresteg: bare JRE + jar ----------
FROM eclipse-temurin:25-jre
ARG TJENESTE
WORKDIR /app
RUN useradd --system --uid 1001 spring
USER spring
COPY --from=build /app/${TJENESTE}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
