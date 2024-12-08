package com.fourthwall.fury.presentation

import com.fourthwall.fury.core.BookedScreening
import java.time.LocalDateTime
import java.util.UUID

data class BookedScreeningDTO(val booking: UUID, val imdbID: String, val price: String, val showTime: LocalDateTime) {
    companion object {
        fun from(bookedScreening: BookedScreening): BookedScreeningDTO =
            BookedScreeningDTO(
                booking = bookedScreening.booking,
                imdbID = bookedScreening.imdbID.value,
                price = bookedScreening.price.value.toString(),
                showTime = bookedScreening.showTime,
            )
    }
}
