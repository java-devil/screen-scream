package com.fourthwall.fury.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.fourthwall.fury.core.UserName
import org.springframework.stereotype.Component

@Component
class UserNameValidator {
    fun validate(value: String): Either<UserNameValidationError, UserName> = either {
        ensure(!value.contains("Warszawa")) { OffensiveUserName }
        UserName(value)
    }
}

sealed interface UserNameValidationError : ValidationError
data object OffensiveUserName : UserNameValidationError
// The remainder is left as an exercise for the reader ;)
