package com.fourthwall.fury.core

import java.math.BigDecimal

interface ReviewBook {
    fun calculateMeanUserScoreOf(imdbID: ImdbID): BigDecimal?
    fun findBy(imdbID: ImdbID, user: UserName): UserScore?
    fun upsert(imdbID: ImdbID, user: UserName, score: UserScore)
}
