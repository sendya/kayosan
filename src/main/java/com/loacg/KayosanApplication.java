package com.loacg;

import com.loacg.kayo.Kayo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramApiException;

@SpringBootApplication
public class KayosanApplication {

	public static void main(String[] args) {
		SpringApplication.run(KayosanApplication.class, args);

		try {
			Kayo.run();

		} catch (TelegramApiException e) {
			System.exit(0);
		}

	}
}
