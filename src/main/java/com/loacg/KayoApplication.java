package com.loacg;

import com.loacg.kayo.Kayo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramApiException;

@SpringBootApplication
public class KayoApplication {

	public static void main(String[] args) {
		// SpringApplication.run(KayoApplication.class, args);

		try {
			Kayo.run();
		} catch (TelegramApiException e) {
			System.exit(0);
		}
	}
}
