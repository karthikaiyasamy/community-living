package com.clbc;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClbcBackendApplication {

	static {
		Dotenv dotenv = Dotenv.configure()
				.directory("..")
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();
		
		dotenv.entries().forEach(entry -> {
			if (System.getProperty(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(ClbcBackendApplication.class, args);
	}
}
