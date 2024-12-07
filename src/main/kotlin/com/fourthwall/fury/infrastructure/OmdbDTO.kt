package com.fourthwall.fury.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty
import com.fourthwall.fury.core.ImdbID
import com.fourthwall.fury.core.ImdbScore
import com.fourthwall.fury.core.Movie
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

data class OmdbDTO (

    @JsonProperty("Title")
    val title: String,
    @JsonProperty("Plot")
    val plot: String,
    @JsonProperty("Released")
    val released: String,
    @JsonProperty("Runtime")
    val runtime: String,
    @JsonProperty("imdbRating")
    val imdbRating: Double,
) {
    fun toMovie(imdbID: ImdbID): Movie {
        val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
            .appendPattern("dd MMM yyyy")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()

        return Movie(
            imdbID = imdbID,
            name = title,
            description = plot,
            released = LocalDateTime.parse(released, formatter).toLocalDate(),
            runtime = Duration.ofMinutes(runtime.split(" ")[0].toLong()),
            imdbScore = ImdbScore(imdbRating)
        )
    }
}
