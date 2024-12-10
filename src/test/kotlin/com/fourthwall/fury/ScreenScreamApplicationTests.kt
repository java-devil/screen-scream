package com.fourthwall.fury

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(PersistenceConfiguration::class)
class ScreenScreamApplicationTests {

	@Test
	fun contextLoads() {}
}
