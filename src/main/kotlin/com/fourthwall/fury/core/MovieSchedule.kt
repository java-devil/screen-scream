package com.fourthwall.fury.core

import java.util.UUID

interface MovieSchedule {
    fun findAll(): Collection<BookedScreening>
    fun findBy(imdbID: ImdbID): Collection<BookedScreening>
    fun findBy(booking: UUID): BookedScreening?
    fun book(screening: Screening): UUID?
    fun free(booking: UUID): Boolean
}
