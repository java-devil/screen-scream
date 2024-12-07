package com.fourthwall.fury.core

import java.time.Duration
import java.time.LocalDate

data class Movie (
    val imdbID: ImdbID,
    val name: String,
    val description: String,
    val released: LocalDate,
    val runtime: Duration,
    val imdbScore: ImdbScore,
)
