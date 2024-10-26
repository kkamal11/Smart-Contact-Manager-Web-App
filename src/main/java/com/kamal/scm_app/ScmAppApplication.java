package com.kamal.scm_app;

import com.kamal.scm_app.utils.AppConstants;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ScmAppApplication {

	@Value("${server.port}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(ScmAppApplication.class, args);
	}

	//This method runs after the application starts and prints the server port
	@PostConstruct
	public void printServerPort() {
		System.out.println("Application Running at http://localhost:" + port + "/");
	}

}
