package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.fourthwall.fury.core.*
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UserScoreValidator {
    val zero = BigDecimal("0.0")
    val one = BigDecimal("1.0")
    val half = BigDecimal("0.5")
    val five = BigDecimal("5.0")

    fun validate(value: BigDecimal): Either<UserScoreValidationError, UserScore> = either {
        val reminder = value.remainder(one)
        ensure(reminder == zero || reminder == half) { UserScorePrecisionViolation }
        ensure(value >= one) { UserScoreMinBoundViolation }
        ensure(value <= five) { UserScoreMaxBoundViolation }
        UserScore(value)
    }
}

sealed interface UserScoreValidationError : ValidationError
data object UserScorePrecisionViolation : UserScoreValidationError
data object UserScoreMinBoundViolation : UserScoreValidationError
data object UserScoreMaxBoundViolation : UserScoreValidationError
