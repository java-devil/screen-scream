package com.fourthwall.fury.core

import java.time.LocalDateTime
import java.util.*

data class BookedScreening(val booking: UUID, val imdbID: ImdbID, val price: Price, val showTime: LocalDateTime) {
    companion object {
        fun from(screening: Screening, booking: UUID): BookedScreening =
            BookedScreening(
                booking = booking,
                imdbID = screening.imdbID,
                price = screening.price,
                showTime = screening.showTime
            )
    }
}
