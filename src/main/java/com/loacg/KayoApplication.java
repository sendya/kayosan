package com.loacg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KayoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KayoApplication.class, args);

/*		try {
			Kayo.run();
		} catch (TelegramApiException e) {
			System.exit(0);
		}*/
	}
}
