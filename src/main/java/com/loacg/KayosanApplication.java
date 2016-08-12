package com.loacg;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.handlers.DirectionsHandlers;
import com.loacg.utils.SudoExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@RestController
@SpringBootApplication
public class KayosanApplication {

	private final Logger logger = LoggerFactory.getLogger(KayosanApplication.class);

	private BotSession session;
	@Autowired private BotConfig botConfig;
	@Autowired private DirectionsHandlers bot;

	@RequestMapping("/") public String home() {
		return "This page is not available";
	}

	@PostConstruct public void start() {
		TelegramBotsApi api = new TelegramBotsApi();
		try {
			session = api.registerBot(bot);
			logger.info("{} is running", bot.getBotUsername());
			bot.init();
		} catch (TelegramApiException e) {
			logger.error("Failed to register bot {} due to error {}: {}", bot.getBotUsername(), e.getMessage(), e.getApiResponse());
		}
	}

	@PreDestroy public void stop() {
		if (session != null) {
			session.close();
		}
	}

	public static void main(String[] args) {
		try {
			SudoExecutor.run(SudoExecutor.buildCommands("cat /var/log/robot/spring.log"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SpringApplication.run(KayosanApplication.class, args);
	}
}
