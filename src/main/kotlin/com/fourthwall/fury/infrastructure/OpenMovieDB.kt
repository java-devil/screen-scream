package com.fourthwall.fury.infrastructure

import com.fourthwall.fury.AppConfig
import com.fourthwall.fury.core.ImdbID
import com.fourthwall.fury.core.Movie
import com.fourthwall.fury.core.MovieRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClient

@Repository
@Suppress("unused")
class OpenMovieDB(private val omdbClient: RestClient, private val appConfig: AppConfig) : MovieRepository {

    private val logger: Logger = LoggerFactory.getLogger(OpenMovieDB::class.java)

    private val furiousMovies: Map<ImdbID, Movie?> = appConfig
        .furiousMovies
        .map { ImdbID(it) }
        .associateWith { lookUp(it) }

    override fun findAll(): Collection<Movie> = furiousMovies.values.filterNotNull()

    override fun findBy(imdbId: ImdbID): Movie? = furiousMovies[imdbId]

    private fun lookUp(imdbId: ImdbID): Movie? {
        val response = omdbClient.get()
            .uri("/?apikey=${appConfig.omdbApiKey}&i=${imdbId.value}")
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, response -> logger.warn("OMDB responded with 4xx Error: ${response.body}, for IMDB ID: $imdbId") }
            .onStatus({ it.is5xxServerError }) { _, response -> logger.warn("OMDB responded with 5xx Error: ${response.body}, for IMDB ID: $imdbId") }

        // This horrible code is sponsored by OMDB responding with 200s to invalid IMDB ID's
        return kotlin.runCatching { response.body(OmdbDTO::class.java)?.toMovie(imdbId) }
            .fold({ it }, { recoverFromDeserializationError((imdbId)) })
    }

    private fun recoverFromDeserializationError(imdbId: ImdbID): Movie? {
        logger.error("OMDB responded with NOT FOUND, for IMDB ID: $imdbId")
        return null
    }
}
