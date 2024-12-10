package com.fourthwall.fury.presentation

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

class UserReviewDTO(
    @field:Schema(
        description = "Score awarded by the user to the movie, from 1.0 to 5.0 in increments of 0.5",
        minimum = "1.0",
        maximum = "5.0",
        example = "5.0",
        pattern = """^\d\.(0|5)$""",
    )
    val userScore: BigDecimal,
)
