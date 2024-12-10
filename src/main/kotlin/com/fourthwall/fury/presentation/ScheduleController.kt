package com.fourthwall.fury.presentation

import com.fourthwall.fury.application.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Movie Schedule")
@RequestMapping("api/v1/schedules")
class ScheduleController(private val movieScheduler: MovieScheduler) {

    @GetMapping
    @Operation(summary = "Retrieve all screenings of all F&F movies")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successfully retrieved movie screenings")])
    fun findAll(): ResponseEntity<Collection<BookedScreeningDTO>> = ResponseEntity.ok(movieScheduler.findAll())

    @GetMapping("/{imdbID}")
    @Operation(summary = "Retrieve all screenings of the specified F&F movie")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved movie screenings"),
        ApiResponse(responseCode = "404", description = "IMDB ID is valid, but not a F&F movie"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid")])
    fun findBy(@Parameter(description = "IMDB ID of the F&F movie", example = "tt0232500") @PathVariable imdbID: String): ResponseEntity<*> =
        movieScheduler.findBy(imdbID)
            .map { ResponseEntity.ok(it) }
            .mapLeft {
                when (it) {
                    is MovieInsufficientlyFurious -> ResponseEntity.notFound().build()
                    is ImdbValidationError -> ResponseEntity.unprocessableEntity().body(it)
                }
            }
            .fold({ it }, { it })

    @PostMapping
    @Operation(summary = "Book a movie screening")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Screening booked successfully"),
        ApiResponse(responseCode = "404", description = "IMDB ID is valid, but not a F&F movie"),
        ApiResponse(responseCode = "409", description = "Booking conflict with different movie"),
        ApiResponse(responseCode = "422", description = "IMDB ID is invalid")])
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
    @Operation(summary = "Removed a booked movie screening")
    @ApiResponses(value = [
            ApiResponse(responseCode = "204", description = "Movie booking removed successfully"),
            ApiResponse(responseCode = "404", description = "No such Booking UUID in DB")])
    fun delete(@Parameter(description = "The UUID of the booking to remove") @PathVariable booking: UUID): ResponseEntity<*> =
        when (movieScheduler.free(booking)) {
            null -> ResponseEntity.noContent().build<Any>()
            else -> ResponseEntity.notFound().build<Any>()
        }
}
