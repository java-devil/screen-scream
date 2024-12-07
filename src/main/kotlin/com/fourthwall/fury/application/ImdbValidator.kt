package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.fourthwall.fury.AppConfig
import com.fourthwall.fury.core.ImdbID
import org.springframework.stereotype.Component

@Component
class ImdbValidator(private val appConfig: AppConfig) {
    fun validate(value: String): Either<ImdbValidationError, ImdbID> = either {
        val prefix = value.take(2)
        val suffix = value.drop(2)
        ensure(value.length == 9) { ImdbSizeValidationError }
        ensure(prefix == "tt") { ImdbPrefixValidationError }
        ensure(suffix.all { it.isDigit() }) { ImdbSuffixValidationError }
        ensure(value in appConfig.furiousMovies) { MovieInsufficientlyFurious }
        ImdbID(value)
    }
}

sealed interface ImdbValidationError : ValidationError
data object ImdbSizeValidationError : ImdbValidationError
data object ImdbPrefixValidationError : ImdbValidationError
data object ImdbSuffixValidationError : ImdbValidationError
data object MovieInsufficientlyFurious : ImdbValidationError
