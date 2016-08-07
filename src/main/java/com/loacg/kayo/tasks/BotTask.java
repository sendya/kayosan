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

    @Scheduled(cron = "0 15 */1 * * *")
    public void delaySendHitokoto() {

        if (bot != null) {
            bot.delaySendHitokoto();
        }

        System.out.println("delay send hitokoto");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void delaySendChime() {
        if(bot != null) {
            bot.delaySendChime();
        }
    }
}
