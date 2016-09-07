package com.loacg.kayo.tasks;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/7/2016 4:28 PM
 */
@Component
@Configurable
@EnableScheduling
public class BotTask {

    @Autowired
    private DirectionsHandlers bot;

    @Scheduled(cron = "0 0 8,23 * * *")
    public void delaySendGoodMorningAndBye() {
        if (bot != null && bot.getBotStatus()) {
            bot.delaySendGoodMorningAndBye();
        }
    }

    @Scheduled(cron = "0 15 7-23/1 * * *")
    public void delaySendHitokoto() {
        if (bot != null  && bot.getBotStatus()) {
            bot.delaySendHitokoto();
        }
    }

    @Scheduled(cron = "0 0 0,7,12 * * *")
    public void delaySendChime() {
        if(bot != null  && bot.getBotStatus()) {
            bot.delaySendChime();
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void blogCommentCheck() {
        if(bot != null) {
            bot.blogCommentCheck();
        }
    }
}
