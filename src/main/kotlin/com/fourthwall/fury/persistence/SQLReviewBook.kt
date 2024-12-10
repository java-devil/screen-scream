package com.fourthwall.fury.persistence

import com.fourthwall.fury.core.ImdbID
import com.fourthwall.fury.core.ReviewBook
import com.fourthwall.fury.core.UserName
import com.fourthwall.fury.core.UserScore
import nu.studer.sample.tables.MovieReviews.MOVIE_REVIEWS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.RoundingMode

@Primary
@Repository
@Suppress("unused")
class SQLReviewBook(private val db: DSLContext) : ReviewBook {

    override fun calculateMeanUserScoreOf(imdbID: ImdbID): BigDecimal? =
        db.select(DSL.avg(MOVIE_REVIEWS.USER_SCORE))
            .from(MOVIE_REVIEWS)
            .where(MOVIE_REVIEWS.IMDB_ID.eq(imdbID.value))
            .fetchOne(0, BigDecimal::class.java)
            ?.setScale(1, RoundingMode.HALF_UP)

    override fun findBy(imdbID: ImdbID, user: UserName): UserScore? =
        db.select(MOVIE_REVIEWS.USER_SCORE)
            .from(MOVIE_REVIEWS)
            .where(MOVIE_REVIEWS.IMDB_ID.eq(imdbID.value))
            .and(MOVIE_REVIEWS.USER_NAME.eq(user.value))
            .fetchOne(0, BigDecimal::class.java)
            ?.let { UserScore(it) }

    override fun upsert(imdbID: ImdbID, user: UserName, score: UserScore) {
        db.insertInto(MOVIE_REVIEWS)
            .set(MOVIE_REVIEWS.IMDB_ID, imdbID.value)
            .set(MOVIE_REVIEWS.USER_NAME, user.value)
            .set(MOVIE_REVIEWS.USER_SCORE, score.value)
            .onDuplicateKeyUpdate()
            .set(MOVIE_REVIEWS.USER_SCORE, score.value)
            .execute()
    }
}
