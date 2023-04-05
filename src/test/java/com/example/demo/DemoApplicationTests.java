package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void shouldRetrieveSSE() {
		var flux = client.get()
				.uri("/subscribe")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.returnResult(String.class)
				.getResponseBody()
				.log();

		client.get()
				.uri("/publish/{data}", "Alex")
				.exchange();

		StepVerifier.create(flux)
				.expectNext("data", "Alex")
				.thenCancel()
				.verify();

	}

}
