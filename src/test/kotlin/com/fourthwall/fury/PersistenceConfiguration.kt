package com.fourthwall.fury

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration
class PersistenceConfiguration {
    @Bean
    @ServiceConnection
    @Suppress("unused")
    fun dockerizedDB() = PostgreSQLContainer("postgres:17-alpine")
        .apply {
            withReuse(true)
            start()
        }
}
