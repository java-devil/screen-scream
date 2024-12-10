# How to deploy?

```
./gradlew jibDockerBuild
./gradlew composeUp -DOMDB_API_KEY=${INSERT_YOUR_SUPER_SECRET_OMDB_API_KEY_HERE}
```

Yeah... that's it.

# What would I do differently?

### In a zeroth iteration to immediately unblock a mobile developer?

I would mostly write the controllers as is and mock all their responses.

### In a second "production worthy" iteration?
- **AUTHENTICATION.** Of any kind :P Neither the "admin" endpoints, nor the user-specific endpoints are protected in any way.
- There is no need for a robust web client in the "first iteration" of an API. So I intentionally invested precisely zero time into it. However, these concerns need to be addressed:
  - retries
  - timeouts
  - circuit breakers
  - logging interceptors
  - WireMock should be used to simulate OMDB failure
- Likewise, I invested zero time into logging and even less so into structured metrics. Especially the `Either<Error, T>` monad needs to be populated with relevant error messages and bubbled up to the presentation layer.
- JOOQ errors should be represented in the type system.
- I would describe the required infrastructure with IaaC (preferably Terraform)
- I would enforce the company best practices on the source code repository
- I would describe the application deployments with a GitOps pipeline
- I would populate the secrets from a cloud KVS (preferably Vault)
- This is a business decision, but there is much room to improve the Scheduler's conflict detection:
  - we know what's the runtime of our movies
  - our cinema can possibly have multiple rooms
  - our brand can possibly have multiple cinemas
  - minimum buffers should probably be considered
- IMDB ID serves as a convenient primary key for a first draft. Technically, it's perfectly sufficient. However... it still would be best practice to substitute it with an artificially primary key since we have no control over it, and we may wish to preserve the possibility to use other data sources in the future.
- As a very minimum, paging should be introduced ASAP. Other data manipulation possibilities should be considered whenever there is a business decision to do so.
- Configure a Spring Boot Actuator.
- On-Call should be set up.

### What could be considered as "overkill" for a first iteration?
#### ...but I did it anyway for fun
- Flyway
- JOOQ Code Gen
- Docker Compose
- Test Containers
- **Extensive Validation**
- **Error Representation as Monads**

### Non-Obvious / Controversial Decisions
- I decided to interpret the "homework" such that the cinema doesn't intend to do business with any other movies than the F&F franchise as a strict rule. In a "real world" scenario this should obviously be consulted with a PO/PM/PdM.
- The `InMemoryRepositories` are currently "useless." I knew this before I set out. I wrote them to show my "process" when another developer is at risk of being blocked. That being said, some would say they could be used in unit tests vs mocks.
- As for unit tests... TBH I don't know what to think anymore. I used to be an adherent believer of the church of TDD, but the "conceptual" benefits are mostly nullified by GenAI. While the "unit" of the microservice seems to be more and more an HTTP call... Whatever is the company consensus, I'm fine with adhering to. In any event - I skipped unit tests in this excercise.
- I considered `PUT /api/v1/movies/{imdbID}/schedules` vs `PUT /api/v1/shedules`, however in the end the ability to query schedules of many movies (with possibly advanced filtering) at the same time won over.
- The use of `show_time` as the primary key of `movie_schedule` may be surprising given the presence of a unique `booking` ID. However, when you think of the ways a Time Schedule would be used - this is probably going to be the most read heavy part of the DB. Ofc should be measured in reality.
- Perhaps controversially, I opted for JOOQ vs JPA. Personally, I usually find the "problem" (SQL) that ORMs are trying to simplify to be simpler than the simplification itself.
- As a minor simplification, I allowed myself to assume that there is only one currency in the world in the `Price` value class ;)
