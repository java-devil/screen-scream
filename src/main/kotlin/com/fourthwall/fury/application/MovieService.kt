package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.flatMap
import com.fourthwall.fury.core.MovieRepository
import com.fourthwall.fury.presentation.MovieDTO
import org.springframework.stereotype.Service

@Service
class MovieService(private val movieRepository: MovieRepository, private val imdbValidator: ImdbValidator) {
    fun findAll(): Collection<MovieDTO> = movieRepository.findAll().map { MovieDTO.from(it) }

    fun findBy(imdbID : String): Either<ImdbError, MovieDTO> =
        imdbValidator.validate(imdbID)
            .map { movieRepository.findBy(it) }
            .flatMap {
                when (it) {
                    null -> Either.Left(MovieInsufficientlyFurious)
                    else -> Either.Right(it)
                }
            }
            .map { MovieDTO.from(it) }
}
