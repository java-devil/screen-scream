package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.fourthwall.fury.core.ReviewBook
import com.fourthwall.fury.presentation.UserReviewDTO
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class MovieReviewer(
    private val reviewBook: ReviewBook,
    private val imdbValidator: ImdbValidator,
    private val userNameValidator: UserNameValidator,
    private val userScoreValidator: UserScoreValidator,
) {
    fun checkHiveMindReviewOf(imdbID: String): Either<CheckUserReviewsError, BigDecimal> = imdbValidator.validate(imdbID)
        .map { reviewBook.calculateMeanUserScoreOf(it) }
        .flatMap {
            when (it) {
                null -> Either.Left(MovieNotYetReviewedError)
                else -> Either.Right(it)
            }
        }

    fun checkReviewBy(imdbID: String, user: String): Either<CheckUserReviewsError, UserReviewDTO> = either {
        val movieID = imdbValidator.validate(imdbID).bind()
        val userName = userNameValidator.validate(user).bind()
        val userScore = reviewBook.findBy(movieID, userName) ?: raise(MovieNotYetReviewedError)
        UserReviewDTO(userScore.value)
    }

    fun review(imdbID: String, user: String, score: BigDecimal): ValidationError? = either {
        val movieID = imdbValidator.validate(imdbID).bind()
        val userName = userNameValidator.validate(user).bind()
        val userScore = userScoreValidator.validate(score).bind()
        reviewBook.upsert(movieID, userName, userScore)
    }.fold({ it }, { null })
}
