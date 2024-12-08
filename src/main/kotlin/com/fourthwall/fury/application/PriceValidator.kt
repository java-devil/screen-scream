package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.fourthwall.fury.core.*
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PriceValidator {
    fun validate(value: String): Either<PriceValidationError, Price> = either {
        val decimal = value.toBigDecimalOrNull() ?: raise(NonNumericPriceError)
        ensure(decimal.scale() <= 2) { PricePrecisionError }
        ensure(decimal > BigDecimal.ZERO) { PriceMinBoundError }
        Price(decimal)
    }
}

sealed interface PriceValidationError : ValidationError
data object NonNumericPriceError : PriceValidationError
data object PricePrecisionError : PriceValidationError
data object PriceMinBoundError : PriceValidationError
