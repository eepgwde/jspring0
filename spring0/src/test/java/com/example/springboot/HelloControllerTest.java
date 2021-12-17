package com.example.springboot;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.centa.clients.Element0;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;


@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {
	private static final Log LOG = LogFactory.getLog(HelloControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	Element0 element0;

	@BeforeEach
	public void setUp() {
	}

	@PostConstruct
	void checkUp() {
		LOG.info(element0);
		if (element0 != null) {
			LOG.info(element0.tdir);
			LOG.info(element0.name);
			LOG.info(element0.stringValue);
		}
	}

	@Test
	public void getHello() throws Exception {
		LOG.info("test get-hello");
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from Spring Boot!")));
	}
}
