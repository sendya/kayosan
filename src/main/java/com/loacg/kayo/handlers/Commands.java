package com.loacg.kayo.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/9/12 15:34
 */
public class Commands {

    private static final Logger logger = LoggerFactory.getLogger(Commands.class);

    @Autowired private Directions directions;

    public static void handler(Message message) {

        String text = message.getText();
        logger.info("User {}[{}] call command \"{}\" from \"{}\" , messageId: {}", message.getFrom().getUserName(), message.getFrom().getId(), text, message.getChatId(), message.getMessageId());


        return;
    }
}
