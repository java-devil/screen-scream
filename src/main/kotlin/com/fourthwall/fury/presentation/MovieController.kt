package com.fourthwall.fury.presentation

import com.fourthwall.fury.application.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Suppress("unused")
@RequestMapping("api/v1/movies")
class MovieController(private val movieService: MovieService, private val movieReviewer: MovieReviewer) {

    @GetMapping
    @Tag(name = "Movie Info")
    @Operation(summary = "Retrieve a comprehensive description of all F&F movies")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successfully retrieved movie descriptions")])
    fun findAll(): ResponseEntity<*> = ResponseEntity.ok(movieService.findAll())

    @GetMapping("/{imdbID}")
    @Tag(name = "Movie Info")
    @Operation(summary = "Retrieve a comprehensive description of the specified F&F movie")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved movie description"),
        ApiResponse(responseCode = "404", description = "IMDB ID is valid, but not a F&F movie"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid"),
        ApiResponse(responseCode = "503", description = "OMDB is unavailable")])
    fun findBy(@Parameter(description = "IMDB ID of the F&F movie", example = "tt0232500") @PathVariable imdbID: String): ResponseEntity<*> =
        movieService.findBy(imdbID)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ValidationError -> ResponseEntity.unprocessableEntity().body(it)
                    is OmdbNoResponseError -> ResponseEntity.status(SERVICE_UNAVAILABLE).body(it)
                }
            }
            .fold({ it }, { it })

    @Tag(name = "Movie Reviews")
    @GetMapping("/{imdbID}/reviews")
    @Operation(summary = "Calculate the mean of all user scores of the specified F&F movie")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully calculated mean score"),
        ApiResponse(responseCode = "404", description = "The F&F movie is not yet reviewed by any user"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid")])
    fun checkHiveMindReviewOf(@Parameter(description = "IMDB ID of the F&F movie", example = "tt0232500") @PathVariable imdbID: String): ResponseEntity<*> =
        movieReviewer.checkHiveMindReviewOf(imdbID)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is MovieNotYetReviewedError -> ResponseEntity.notFound().build()
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ValidationError -> ResponseEntity.unprocessableEntity().body(it)
                }
            }
            .fold({ it }, { it })

    @Tag(name = "Movie Reviews")
    @GetMapping("/{imdbID}/reviews/{user}")
    @Operation(summary = "Retrieve the review of the specified F&F movie by the specified user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved user review, from 1.0 to 5.0 in increments of 0.1"),
        ApiResponse(responseCode = "404", description = "The F&F movie is not yet reviewed by this specific user"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid or username is invalid")])
    fun checkHiveMindReviewOf(
        @Parameter(description = "IMDB ID of the F&F movie", example = "tt0232500") @PathVariable imdbID: String,
        @Parameter(description = "Username of the reviewer", example = "Bobby") @PathVariable user: String
    ): ResponseEntity<*> =
        movieReviewer.checkReviewBy(imdbID, user)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is MovieNotYetReviewedError -> ResponseEntity.notFound().build()
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ValidationError -> ResponseEntity.unprocessableEntity().body(it)
                }
            }
            .fold({ it }, { it })

    @Tag(name = "Movie Reviews")
    @PutMapping("/{imdbID}/reviews/{user}")
    @Operation(summary = "Submit or modify the user review of the specified F&F movie")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Review successfully submitted or modified"),
        ApiResponse(responseCode = "404", description = "IMDB ID is valid, but not a F&F movie"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid")])
    fun review(
        @Parameter(description = "IMDB ID of the F&F movie", example = "tt0232500") @PathVariable imdbID: String,
        @Parameter(description = "Username of the reviewer", example = "Bobby") @PathVariable user: String,
        @RequestBody reviewDTO: UserReviewDTO
    ): ResponseEntity<*> =
        when (val error = movieReviewer.review(imdbID, user, reviewDTO.userScore)) {
            null -> ResponseEntity.ok().build<Any>()
            is MovieInsufficientlyFurious -> ResponseEntity.notFound().build<Any>()
            is ValidationError -> ResponseEntity.unprocessableEntity().body(error)
        }
}
