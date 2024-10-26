package com.kamal.scm_app;

import com.kamal.scm_app.service.email.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScmAppApplicationTests {

	@Autowired
	private EmailService service;

	@Test
	void contextLoads() {
	}

	//Email Test
	@Test
	void sendEmailTest() throws MessagingException {
		service.sendRegisterEmailWithHtml(
				"kamal",
				"kishorkamal7091@gmail.com",
				"Testing mail",
				"Testing mail body");
	}

}
