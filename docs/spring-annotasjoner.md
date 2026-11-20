# Spring-annotasjoner i valgprosjektet

En annotasjon (`@Noe`) er en merkelapp på en klasse, metode, parameter eller et felt. Den gjør ingenting selv; Spring leser den ved oppstart og bygger oppførsel rundt det merkede. Dokumentet dekker annotasjonene som brukes i de fire tjenestene (`innrapportering`, `opptelling`, `fylkesoversikt`, `revisjon`), gruppert etter hva de brukes til. Hver oppføring sier **hvor** annotasjonen står og **hva** den gjør.

## Oppstart og konfigurasjon

Disse styrer hvordan appen starter og hvilke objekter (beans) Spring lager.

| Annotasjon | Hvor | Hva den gjør | I prosjektet |
| --- | --- | --- | --- |
| `@SpringBootApplication` | Hovedklassen | Slår på auto-konfigurasjon og komponent-skanning av pakken klassen ligger i og alle underpakker. Derfor må all kode ligge under `no.valg.<tjeneste>`. | `InnrapporteringApplication`, `OpptellingApplication` m.fl. |
| `@Configuration` | Klasse | Sier at klassen inneholder `@Bean`-metoder som lager objekter Spring skal administrere. | `RabbitConfig` i alle tjenestene |
| `@Bean` | Metode i en `@Configuration`-klasse | Returverdien registreres som en bean. Metodenavnet blir bean-navnet. | `resultatExchange()`, `fylkeQueue()`, `deadLetterQueue()` |
| `@Value("\${...}")` | Konstruktørparameter eller felt | Henter én verdi fra `application.yml` (eller miljøvariabel). | `@Value("\${fylkesoversikt.fylke}")` i `fylkesoversikt` |
| `@ConditionalOnProperty` | Klasse eller `@Bean`-metode | Lager beanen bare hvis en property har en bestemt verdi. Ellers finnes den ikke. | `ValgnattSimulator` lages bare når `simulator.enabled=true` |
| `@EnableScheduling` | Hovedklassen (eller en `@Configuration`) | Slår på støtten for `@Scheduled`. Uten den kjøres planlagte metoder aldri, og ingen feil meldes. | `InnrapporteringApplication` |
| `@Autowired` | Konstruktørparameter eller felt | Ber Spring sette inn en bean. Unødvendig når klassen har én konstruktør; da gjør Spring det automatisk. | Bare i testene (`@Autowired private val service`) |

## Komponenter

Alle disse står på en klasse og gjør den til en bean som Spring finner ved komponent-skanning. Teknisk er de varianter av `@Component`; navnet forteller leseren hvilken rolle klassen har.

| Annotasjon | Rolle | Hva den gjør i tillegg | I prosjektet |
| --- | --- | --- | --- |
| `@Component` | Generell bean | Ingenting ekstra. Brukes når ingen av de andre passer. | `NasjonalOpptellingListener`, `FylkesoversiktListener`, `RevisjonListener`, `ValgnattSimulator` |
| `@Service` | Forretningslogikk | Ingenting ekstra; ren dokumentasjon av rollen. | `ResultatPublisher`, `OpptellingService`, `Fylkesoversikt` |
| `@Repository` | Databasetilgang | Oversetter databasefeil til Springs egne unntak. Trengs ikke på Spring Data-interfaces (`JpaRepository`), som blir repositories automatisk. | Ikke brukt; `opptelling` bruker `JpaRepository`-interfaces |
| `@RestController` | HTTP-endepunkter | `@Controller` + `@ResponseBody`: returverdien fra metodene skrives som JSON (eller tekst) i svaret, ikke som navn på en HTML-mal. | `ResultatController`, `OpptellingController`, `FylkesoversiktController`, `RevisjonController`, `RootController` |

## Web og REST

Disse hører hjemme i en `@RestController` og kobler HTTP-forespørsler til Kotlin-metoder.

| Annotasjon | Hvor | Hva den gjør | I prosjektet |
| --- | --- | --- | --- |
| `@RequestMapping("/sti")` | Kontrollerklassen | Felles sti-prefiks for alle metodene i klassen. | `/api/resultater`, `/api/opptelling` |
| `@GetMapping("/sti")` | Metode | Metoden svarer på `GET` til stien (prefiks + sti). | `/api/fylkesoversikt`, `/api/opptelling/nasjonal`, `/api/revisjon`, `/` |
| `@PostMapping` | Metode | Metoden svarer på `POST`. Uten sti brukes bare klassens prefiks. | `POST /api/resultater` |
| `@RequestBody` | Metodeparameter | Leser JSON fra forespørselens kropp og gjør den om til et Kotlin-objekt (Jackson). | `melding: ValgresultatMelding` |
| `@RequestParam` | Metodeparameter | Leser en query-parameter (`?antall=5`). `defaultValue` brukes når den mangler. | `antall` i `RevisjonController` |
| `@Valid` | Metodeparameter (sammen med `@RequestBody`) | Kjører valideringsreglene på objektet. Bryter det en regel, svarer Spring `400 Bad Request` før metoden kalles. | `ResultatController.motta` |
| `@NotBlank`, `@NotEmpty` | Felt i dataklassen | Valideringsreglene `@Valid` sjekker: tekst kan ikke være tom, map kan ikke være tom. I Kotlin skrives de `@field:NotBlank` (se siste avsnitt). | `ValgresultatMelding` i `innrapportering` |

## RabbitMQ

`@RabbitListener(queues = [...])` står på en metode i en bean, vanligvis en `@Component`. Spring starter en konsument på køen og kaller metoden for hver melding.

- **Parametertypen bestemmer konverteringen.** `ValgresultatMelding` gjør JSON om til et objekt via `MessageConverter` (`opptelling`, `fylkesoversikt`). `Message` gir rå bytes uten konvertering (`revisjon`).
- **Unntak betyr avvisning.** Kaster metoden et unntak, avvises meldingen. Med `default-requeue-rejected: false` går den da til `valg.dlq` via DLX i stedet for tilbake i køen.
- **Kønavnet kan beregnes.** `#{fylkeQueue.name}` er et SpEL-uttrykk som henter navnet fra beanen `fylkeQueue`, slik at `fylkesoversikt` lytter på `valg.vestfold.queue` uten at navnet er hardkodet.

Køer, exchanges og bindinger lages ikke med annotasjoner her, men som `@Bean`-metoder i `RabbitConfig`.

## Database (JPA)

Bare `opptelling` bruker JPA. `revisjon` skriver SQL direkte med `JdbcClient` og trenger ingen av disse.

| Annotasjon | Hvor | Hva den gjør | I prosjektet |
| --- | --- | --- | --- |
| `@Entity` | Klasse | Klassen tilsvarer en tabell; hvert objekt er en rad. | `PartiStemmer`, `BehandletMelding` |
| `@Table(name = "...")` | Entitetsklassen | Navnet på tabellen. Uten den gjetter JPA navnet ut fra klassenavnet. | `parti_stemmer`, `behandlet_melding` |
| `@Id` | Felt i entiteten | Primærnøkkelen. | `meldingId` i `BehandletMelding` |
| `@GeneratedValue(strategy = IDENTITY)` | `@Id`-feltet | Databasen lager verdien (`BIGSERIAL`), ikke koden. Feltet er derfor `null` før lagring. | `id` i `PartiStemmer` |
| `@Query("...")` | Metode i et repository-interface | Egen JPQL-spørring i stedet for en spørring Spring utleder fra metodenavnet. | Summering av stemmer per parti |
| `@Param("navn")` | Parameter i en `@Query`-metode | Kobler parameteren til `:navn` i spørringen. | I `Repositories.kt` |
| `@Transactional` | Metode (eller klasse) i en `@Service` | Alt i metoden skjer i én databasetransaksjon. Kastes et unntak, rulles alt tilbake. | `OpptellingService.registrer`: idempotens-sjekk og lagring av stemmer skjer sammen eller ikke i det hele tatt |

`@Column` (kolonnenavn, lengde, nullbarhet) brukes ikke; feltnavn i camelCase blir automatisk til snake_case-kolonner.

## Planlegging

`@Scheduled` står på en metode uten parametere i en bean, og Spring kaller den med jevne mellomrom. Det virker bare når `@EnableScheduling` er satt på hovedklassen.

I `ValgnattSimulator` kjører `@Scheduled(fixedRateString = "\${simulator.intervall-ms:1000}")` metoden `sendResultat()` én gang per intervall. Verdien hentes fra `application.yml`, og `:1000` er standardverdien når propertyen mangler. Andre varianter er `fixedDelay` (venter etter forrige kjøring er ferdig) og `cron = "0 0 * * * *"` (bestemte klokkeslett).

## Test

Alle står under `src/test/kotlin`. `@Test` kommer fra JUnit; de andre er Springs.

| Annotasjon | Hvor | Hva den gjør | I prosjektet |
| --- | --- | --- | --- |
| `@Test` | Metode i testklassen | Metoden er en test som JUnit kjører. | `MandatfordelingTest`, `OpptellingServiceIntegrationTest` |
| `@SpringBootTest` | Testklassen | Starter hele Spring-appen før testene, slik at testen kan få ekte beans. Tregere enn en ren enhetstest. | `OpptellingServiceIntegrationTest`, `*ApplicationTests` |
| `@Import(Klasse::class)` | Testklassen | Tar med en ekstra konfigurasjonsklasse i appen som startes. | `@Import(TestcontainersConfiguration::class)` |
| `@TestConfiguration` | Klasse i `src/test` | Som `@Configuration`, men bare for tester, og den plukkes ikke opp automatisk; den må importeres. | `TestcontainersConfiguration` |
| `@ServiceConnection` | `@Bean`-metode som returnerer en Testcontainer | Spring starter containeren og setter URL, brukernavn og passord automatisk. Testen kobler da til en ny Postgres eller RabbitMQ i stedet for den i `docker compose`. | `postgresContainer()`, `rabbitContainer()` |

`MandatfordelingTest` bruker bare `@Test` og ingen Spring-annotasjoner, så den kjører på millisekunder uten Docker.

## Kotlin: `@field:` og andre prefikser

En Kotlin-property (`val meldingId: String`) blir til flere ting i bytekoden: en konstruktørparameter, et felt og en getter. Et prefiks sier hvilken av dem annotasjonen skal sitte på.

- `@field:NotBlank` setter regelen på feltet, der Bean Validation leter. Uten prefikset kan den havne på konstruktørparameteren, og valideringen blir stille hoppet over.
- `@param:` (parameteren) og `@get:` (getteren) finnes også, men trengs ikke i prosjektet.
- Kompilatorflagget `-Xannotation-default-target=param-property` i `build.gradle.kts` gjør standardvalget mer forutsigbart, men `@field:` gjør det tydelig for leseren.
