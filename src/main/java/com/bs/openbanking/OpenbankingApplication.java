package com.bs.openbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpenbankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenbankingApplication.class, args);
	}

}
