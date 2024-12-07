package com.fourthwall.fury.core

interface MovieRepository {
    fun findAll(): Collection<Movie>
    fun findBy(imdbId: ImdbID): Movie?
}
