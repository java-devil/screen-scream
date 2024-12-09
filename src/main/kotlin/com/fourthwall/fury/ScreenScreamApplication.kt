package com.fourthwall.fury

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class ScreenScreamCinemaApplication

fun main(args: Array<String>) {
	runApplication<ScreenScreamCinemaApplication>(*args)
}
