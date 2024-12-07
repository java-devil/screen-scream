package com.fourthwall.fury.presentation

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieControllerIT {

    @LocalServerPort
    private var port: Int = 0
    private val webBrowser: RestClient = RestClient.builder()
        .defaultStatusHandler({ it.is4xxClientError }) { _, response -> println(response.body) }.build()

    @Test
    fun `should respond with a list of all FnF MovieDTOs`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/movies"

        // WHEN:
        val responseType = object : ParameterizedTypeReference<List<MovieDTO>>() {}
        val response = webBrowser.get().uri(url).retrieve().toEntity(responseType)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(9, response.body?.size)
        assertEquals(
            listOf(
                "The Fast and the Furious",
                "2 Fast 2 Furious",
                "The Fast and the Furious: Tokyo Drift",
                "Fast & Furious",
                "Fast Five",
                "Fast & Furious 6",
                "Furious 7",
                "The Fate of the Furious",
                "F9: The Fast Saga",
            ), response.body?.map { it.name }
        )
    }

    @Test
    fun `should respond to a valid FnF IMDB ID with a proper MovieDTO`() {
        // GIVEN:
        val id = "tt0232500"
        val url = "http://localhost:$port/api/v1/movies/$id"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toEntity(MovieDTO::class.java)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("The Fast and the Furious", response.body?.name)
    }

    @Test
    fun `should respond to a valid, but not FnF IMDB ID with HTTP error code 404`() {
        // GIVEN:
        val id = "tt9999999"
        val url = "http://localhost:$port/api/v1/movies/$id"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toBodilessEntity()

        // THEN:
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should respond to an invalid IMDB ID with HTTP error code 422`() {
        // GIVEN:
        val ids = listOf("tt123456", "tt12345678", "xx1234567", "tt123x567")
        val urls = ids.map { "http://localhost:$port/api/v1/movies/$it" }

        // WHEN:
        val responses = urls.map {
            webBrowser.get().uri(it).retrieve().toBodilessEntity()
        }

        // THEN:
        responses.forEach { assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, it.statusCode) }
    }
}
