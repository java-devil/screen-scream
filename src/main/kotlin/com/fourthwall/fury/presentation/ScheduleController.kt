package com.fourthwall.fury.presentation

import com.fourthwall.fury.application.*
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Suppress("unused")
@RequestMapping("api/v1/schedules")
class ScheduleController(private val movieScheduler: MovieScheduler) {

    @GetMapping("/", "")
    fun findAll(): ResponseEntity<Collection<BookedScreeningDTO>> = ResponseEntity.ok(movieScheduler.findAll())

    @GetMapping("/{imdbID}")
    fun findBy(@PathVariable imdbID: String): ResponseEntity<*> =
        movieScheduler.findBy(imdbID)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ImdbValidationError -> ResponseEntity.unprocessableEntity().body(it)
                }
            }
            .fold({ it }, { it })

    @PostMapping("/", "")
    fun book(@RequestBody movieSessionDTO: ScreeningDTO): ResponseEntity<*> =
        movieScheduler.book(movieSessionDTO)
            .map { ResponseEntity.ok().body(it) }
            .mapLeft {
                when (it) {
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ValidationError -> ResponseEntity.unprocessableEntity().body(it)
                    is RoomOverbookedError -> ResponseEntity.status(CONFLICT).body(it)
                }
            }
            .fold({ it }, { it })

    @DeleteMapping("/{booking}")
    fun delete(@PathVariable booking: UUID): ResponseEntity<*> =
        when (movieScheduler.free(booking)) {
            null -> ResponseEntity.noContent().build<Any>()
            else -> ResponseEntity.notFound().build<Any>()
        }
}
