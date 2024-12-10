package com.fourthwall.fury.persistence

import com.fourthwall.fury.core.*
import nu.studer.sample.Tables.MOVIE_SCHEDULE
import nu.studer.sample.tables.records.MovieScheduleRecord
import org.jooq.DSLContext
import org.jooq.Record1

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.util.*

@Primary
@Repository
@Suppress("unused")
class SQLMovieSchedule(private val db: DSLContext) : MovieSchedule {

    override fun findAll(): Collection<BookedScreening> =
        db.select(MOVIE_SCHEDULE)
            .from(MOVIE_SCHEDULE)
            .orderBy(MOVIE_SCHEDULE.SHOW_TIME.desc())
            .fetch { recordMapper(it) }
            .toList()

    override fun findBy(imdbID: ImdbID): Collection<BookedScreening> =
        db.select(MOVIE_SCHEDULE)
            .from(MOVIE_SCHEDULE)
            .where(MOVIE_SCHEDULE.IMDB_ID.eq(imdbID.value))
            .orderBy(MOVIE_SCHEDULE.SHOW_TIME.desc())
            .fetch { recordMapper(it) }
            .toList()

    override fun findBy(booking: UUID): BookedScreening? =
        db.select(MOVIE_SCHEDULE)
            .from(MOVIE_SCHEDULE)
            .where(MOVIE_SCHEDULE.BOOKING.eq(booking))
            .orderBy(MOVIE_SCHEDULE.SHOW_TIME.desc())
            .fetchOne { recordMapper(it) }

    override fun book(screening: Screening): UUID? {
        val booking = UUID.randomUUID()
        val result = db.insertInto(MOVIE_SCHEDULE)
            .set(MOVIE_SCHEDULE.BOOKING, booking)
            .set(MOVIE_SCHEDULE.IMDB_ID, screening.imdbID.value)
            .set(MOVIE_SCHEDULE.PRICE, screening.price.value)
            .set(MOVIE_SCHEDULE.SHOW_TIME, screening.showTime)
            .onDuplicateKeyIgnore()
            .execute()

        return if (result > 0) booking else null
    }

    override fun free(booking: UUID): Boolean {
        val result = db.deleteFrom(MOVIE_SCHEDULE)
            .where(MOVIE_SCHEDULE.BOOKING.eq(booking))
            .execute()

        return result > 0
    }

    private fun recordMapper(dbRecord: Record1<MovieScheduleRecord>): BookedScreening {
        val record = dbRecord.value1()
        val result = BookedScreening(
            record.booking,
            ImdbID(record.imdbId),
            Price(record.price),
            record.showTime
        )

        return result
    }
}
