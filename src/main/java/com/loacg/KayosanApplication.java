package com.loacg;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.handlers.DirectionsHandlers;
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

	@RequestMapping("/memory")
	public String status() {
		double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
		double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);
		double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);

		StringBuffer sb = new StringBuffer()
				.append("JVM 最大可用内存：")
				.append(max).append("MB<br/>")
				.append("当前占用内存：")
				.append(total).append("MB<br/>")
				.append("当前空闲内存：")
				.append(free).append("MB<br/>")
				.append("实际可用内存：")
				.append(max - total + free).append("MB");
		return sb.toString();
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
		SpringApplication.run(KayosanApplication.class, args);
	}
}
