package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.raise.either
import com.fourthwall.fury.core.MovieSchedule
import com.fourthwall.fury.core.Screening
import com.fourthwall.fury.presentation.BookedScreeningDTO
import com.fourthwall.fury.presentation.ScreeningDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class MovieScheduler(
    private val movieSchedule: MovieSchedule,
    private val imdbValidator: ImdbValidator,
    private val priceValidator: PriceValidator,
) {

    fun findAll(): Collection<BookedScreeningDTO> =
        movieSchedule.findAll().map { BookedScreeningDTO.from(it) }

    fun findBy(imdbID: String): Either<ImdbValidationError, Collection<BookedScreeningDTO>> =
        imdbValidator.validate(imdbID)
            .map { movieSchedule.findBy(it)
                .map { BookedScreeningDTO.from(it) }
            }

    fun findBy(booking: UUID): Either<NoSuchBookingError, BookedScreeningDTO> = either {
        when (val bookedScreening = movieSchedule.findBy(booking)) {
            null -> raise(NoSuchBookingError)
            else -> BookedScreeningDTO.from(bookedScreening)
        }
    }

    fun book(screeningDTO: ScreeningDTO): Either<BookMovieScreeningError, UUID> = either {
        val imdbID = imdbValidator.validate(screeningDTO.imdbID).bind()
        val price = priceValidator.validate(screeningDTO.price).bind()
        val screening = Screening(imdbID, price, screeningDTO.showTime)
        movieSchedule.book(screening) ?: raise(RoomOverbookedError)
    }

    fun free(booking: UUID): NoSuchBookingError? =
        if (movieSchedule.free(booking)) { null } else { NoSuchBookingError }
}
