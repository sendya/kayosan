package com.loacg;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class KayosanApplication {

	private final Logger logger = LoggerFactory.getLogger(KayosanApplication.class);

	@Autowired
	private DirectionsHandlers bot;

	private BotSession session;

	public static void main(String[] args) {
		SpringApplication.run(KayosanApplication.class, args);
	}

	@PostConstruct
	public void start() {
		TelegramBotsApi api = new TelegramBotsApi();
		try {
			session = api.registerBot(bot);
			System.out.println("Kayo running");
		} catch (TelegramApiException e) {
			logger.error("Failed to register bot {} due to error {}: {}", bot.getBotUsername(), e.getMessage(), e.getApiResponse());
		}
	}

	@PreDestroy
	public void stop() {
		if (session != null) {
			session.close();
		}
	}

}
