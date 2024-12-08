package com.fourthwall.fury.persistence

import com.fourthwall.fury.core.BookedScreening
import com.fourthwall.fury.core.ImdbID
import com.fourthwall.fury.core.Screening
import com.fourthwall.fury.core.MovieSchedule
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Suppress("unused")
class InMemoryMovieSchedule: MovieSchedule {

    private val db: MutableSet<BookedScreening> = mutableSetOf()

    override fun findAll(): Collection<BookedScreening> = db.toSet()

    override fun findBy(imdbID: ImdbID): Collection<BookedScreening> = db.filter { it.imdbID == imdbID }.toSet()

    override fun findBy(booking: UUID): BookedScreening? = db.find { it.booking == booking }

    override fun book(screening: Screening): UUID? {
        val screeningBooked = db.any { it.showTime == screening.showTime }
        var booking: UUID? = null
        if (!screeningBooked) {
            booking = UUID.randomUUID()
            db += BookedScreening.from(screening, booking)
        }

        return booking
    }

    override fun free(booking: UUID): Boolean {
        val screening = db.find { it.booking == booking }
        val screeningBooked = screening != null
        if (screening != null) {
            db -= screening
        }

        return screeningBooked
    }
}
