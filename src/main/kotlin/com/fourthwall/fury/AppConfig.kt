package com.fourthwall.fury

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@ConfigurationProperties(prefix = "app")
class AppConfig {

    lateinit var omdbApiKey: String
    lateinit var furiousMovies: List<String>

    @Bean
    @Suppress("unused")
    fun omdbClient(): RestClient = RestClient.builder().baseUrl("https://www.omdbapi.com").build()
}
