package com.fourthwall.fury.presentation

import com.fourthwall.fury.application.ImdbValidationError
import com.fourthwall.fury.application.MovieInsufficientlyFurious
import com.fourthwall.fury.application.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("unused")
@RequestMapping("api/v1/movies")
class MovieController(private val movieService: MovieService) {

    @GetMapping("/", "")
    @Suppress("unused")
    fun findAll(): ResponseEntity<*> = ResponseEntity.ok(movieService.findAll())

    @GetMapping("/{imdbID}")
    fun findBy(@PathVariable imdbID: String): ResponseEntity<*> =
        movieService.findBy(imdbID)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is ImdbValidationError -> ResponseEntity.unprocessableEntity().body(it)
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                }
            }
            .fold({ it }, { it })
}
