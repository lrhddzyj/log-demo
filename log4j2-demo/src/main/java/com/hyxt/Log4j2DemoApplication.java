package com.hyxt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Log4j2DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Log4j2DemoApplication.class, args);
	}

	public void run(String... args) throws Exception {
		for (int i = 0; i < 10; i++) {
			log();
			Thread.sleep(500);
		}
	}

	private void log(){
		log.trace("===trace===");
		log.debug("===debug===");
		log.info("===info===");
		log.warn("===warn===");
		log.error("===error===");
	}
}
