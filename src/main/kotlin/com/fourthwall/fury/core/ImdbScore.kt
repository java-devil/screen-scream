package com.fourthwall.fury.core

@JvmInline
value class ImdbScore (val value: Double) {
    init {
        require(value >= 1) { "Score must be ≥ 1" }
        require(value <= 10) { "Score must be ≤ 10" }
    }
}
