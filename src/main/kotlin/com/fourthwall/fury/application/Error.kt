package com.fourthwall.fury.application

sealed interface Error
sealed interface ValidationError : OmdbError, BookMovieScreeningError, FreeMovieBookingError, CheckUserReviewsError

sealed interface OmdbError : Error
data object OmdbNoResponseError : OmdbError

sealed interface BookMovieScreeningError : Error
data object RoomOverbookedError : BookMovieScreeningError

sealed interface FreeMovieBookingError : Error
data object NoSuchBookingError : FreeMovieBookingError

sealed interface CheckUserReviewsError
data object MovieNotYetReviewedError : CheckUserReviewsError
