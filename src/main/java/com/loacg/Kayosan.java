package com.loacg;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.handlers.Directions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class Kayosan {

	private final Logger logger = LoggerFactory.getLogger(Kayosan.class);

	private BotSession session;
	@Autowired private BotConfig botConfig;
	@Autowired private Directions bot;

	@PostConstruct public void start() {
		TelegramBotsApi api = new TelegramBotsApi();
		try {
			session = api.registerBot(bot);
			logger.info("{} is running", bot.getBotUsername());
			// bot.init();
		} catch (TelegramApiException e) {
			e.printStackTrace();
			logger.error("Failed to register bot {} due to error {}", bot.getBotUsername(), e.getMessage());
		}
	}

	@PreDestroy public void stop() {
		if (session != null) {
			session.close();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Kayosan.class, args);
	}
}
