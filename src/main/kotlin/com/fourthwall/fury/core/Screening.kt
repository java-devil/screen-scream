package com.fourthwall.fury.core

import java.time.LocalDateTime

data class Screening(val imdbID: ImdbID, val price: Price, val showTime: LocalDateTime)
