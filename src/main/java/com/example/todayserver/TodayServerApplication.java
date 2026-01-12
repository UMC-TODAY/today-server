package com.example.todayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TodayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodayServerApplication.class, args);
	}

}
