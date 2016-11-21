package com.loacg.kayo.handlers;

import com.loacg.telegrambots.annotation.CommandMapping;
import com.loacg.telegrambots.annotation.MessageType;
import com.loacg.telegrambots.annotation.Permission;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 9/11/2016 4:44 AM
 */
public class CommandHandler {

    @CommandMapping(value = {"/whoami"}, permission = Permission.ALL, messageType = {MessageType.PRIVATE})
    public Message whoami(Update update) {
        Message message = new Message();

        return message;
    }

    @CommandMapping(value = "/ping", permission = Permission.ALL)
    public Message ping(Update update) {

        return null;
    }

    @CommandMapping(value = {"/help", "/start"}, permission = Permission.ALL, messageType = MessageType.ALL)
    public Message help(Message message) {

        return null;
    }

}
