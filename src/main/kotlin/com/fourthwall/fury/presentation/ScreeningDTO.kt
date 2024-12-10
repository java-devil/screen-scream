package com.fourthwall.fury.presentation

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class ScreeningDTO(
    @field:Schema(example = "tt0232500", pattern = """^[a-zA-Z]{2}\d{7}$""")
    val imdbID: String,

    @field:Schema(
        description = "Precise money representation, from 0.01 to 99.99 in increments of 0.01",
        minimum = "0.01",
        maximum = "99.99",
        example = "64.0",
        pattern = """^\d{1,2}\.\d{2}$""",
    )
    val price: String,

    @field:Schema(example = "2025-01-01T00:00:00.000Z")
    val showTime: LocalDateTime,
)
