package com.fourthwall.fury.application

sealed interface Error
sealed interface ValidationError : OmdbError

sealed interface OmdbError : Error
data object OmdbNoResponseError : OmdbError
