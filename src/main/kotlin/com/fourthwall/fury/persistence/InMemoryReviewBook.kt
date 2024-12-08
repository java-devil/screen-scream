package com.fourthwall.fury.persistence

import com.fourthwall.fury.core.*
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
@Suppress("unused")
class InMemoryReviewBook : ReviewBook {

    private val db: MutableMap<UserName, UserReview> = mutableMapOf()

    override fun calculateMeanUserScoreOf(imdbID: ImdbID): BigDecimal? =
        db.values.filter { it.imdbID == imdbID }
            .map { it.score.value.toDouble() }
            .average()
            .toBigDecimal()

    override fun findBy(imdbID: ImdbID, user: UserName): UserScore? =
        db[user]?.score

    override fun upsert(imdbID: ImdbID, user: UserName, score: UserScore) {
        db[user] = UserReview(imdbID, user, score)
    }
}
