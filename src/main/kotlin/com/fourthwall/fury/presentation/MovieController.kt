package com.fourthwall.fury.presentation

import com.fourthwall.fury.application.*
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Suppress("unused")
@RequestMapping("api/v1/movies")
class MovieController(private val movieService: MovieService, private val movieReviewer: MovieReviewer) {

    @GetMapping("/", "")
    fun findAll(): ResponseEntity<*> = ResponseEntity.ok(movieService.findAll())

    @GetMapping("/{imdbID}")
    fun findBy(@PathVariable imdbID: String): ResponseEntity<*> =
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

    @GetMapping("/{imdbID}/reviews", "/{imdbID}/reviews/")
    fun checkHiveMindReviewOf(@PathVariable imdbID: String): ResponseEntity<*> =
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

    @GetMapping("/{imdbID}/reviews/{user}")
    fun checkHiveMindReviewOf(@PathVariable imdbID: String, @PathVariable user: String): ResponseEntity<*> =
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

    @PutMapping("/{imdbID}/reviews/{user}")
    fun review(@PathVariable imdbID: String, @PathVariable user: String, @RequestBody reviewDTO: UserReviewDTO): ResponseEntity<*> =
        when (val error = movieReviewer.review(imdbID, user, reviewDTO.userScore)) {
            null -> ResponseEntity.ok().build<Any>()
            is MovieInsufficientlyFurious -> ResponseEntity.notFound().build<Any>()
            is ValidationError -> ResponseEntity.unprocessableEntity().body(error)
        }
}
