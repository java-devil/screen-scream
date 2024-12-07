package com.fourthwall.fury.presentation

import com.fourthwall.fury.core.Movie

data class MovieDTO (
    val name: String,
    val desc: String,
    val release: String,
    val minutes: Long,
    val imdbScore: Double,
) {
    companion object {
        fun from(movie: Movie): MovieDTO =
            MovieDTO(
                name = movie.name,
                desc = movie.description,
                release = movie.released.toString(),
                minutes = movie.runtime.toMinutes(),
                imdbScore = movie.imdbScore.value,
            )
    }
}
