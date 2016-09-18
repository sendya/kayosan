package com.loacg.kayo.handlers;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.BuildVars;
import com.loacg.kayo.dao.BotInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/9/12 14:41
 */
@Component
public class Directions extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Directions.class);

    @Autowired private BotConfig botConfig;
    @Autowired private BotInfoDao botInfoDao;

    private static long bootTime; // robot start time

    public Directions() {
        bootTime = System.currentTimeMillis();
    }

    @PostConstruct
    public void start() {
        logger.info("Starting {} robot , version : {}", botConfig.getName(), BuildVars.VERSION);

    }

    public void init() {

    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        int time = (int) (System.currentTimeMillis()/1000 - BuildVars.COMMAND_TIME_OUT);

        if (message.getDate() < time) {
            logger.info("User {} call command {} timeout", message.getFrom().getId(), message.getText());
            return;
        }

        try {
            if (!update.hasMessage()) {
                this.handleWelcomeMessage(message);
            }

            if (message.hasText()) {
                new Commands().handler(message);
            }
        } catch (Exception e) {
            logger.error("onUpdateReceived ERROR: {}", e.getMessage());
        }

    }

    private void handleWelcomeMessage(Message message) {
        if (message.getNewChatMember() != null && message.getNewChatMember().getId() != null) {
            try {
                String name = "";
                if (message.getNewChatMember().getFirstName() != null)
                    name = message.getNewChatMember().getFirstName();
                if (message.getNewChatMember().getLastName() != null)
                    name += message.getNewChatMember().getLastName();
                this.hookSendMessage(message.getChatId().toString(), String.format("热烈欢迎 `%s` 加入群组，请先查阅群置顶消息。", name), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
            }
            return;
        }

        if (message.getLeftChatMember() != null && message.getLeftChatMember().getId() != null) {
            try {
                String name = "";
                if (message.getLeftChatMember().getFirstName() != null)
                    name = message.getLeftChatMember().getFirstName();
                if (message.getLeftChatMember().getLastName() != null)
                    name += message.getLeftChatMember().getLastName();
                this.hookSendMessage(message.getChatId().toString(), String.format("群成员 `%s` 离开了群组，-1s。", name), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
            }
            return;
        }
    }

    /**
     * Send message
     * default format text markdown
     *
     * @param chatId
     * @param content
     * @return
     * @throws TelegramApiException
     */
    public Message hookSendMessage(String chatId, String content) throws TelegramApiException {
        return this.hookSendMessage(chatId, content, 0);
    }

    public Message hookSendMessage(String chatId, String content, Integer replyMessageId) throws TelegramApiException {
        return this.hookSendMessage(chatId, content, replyMessageId, BuildVars.FORMAT_NONE);
    }

    public Message hookEditMessage(String chatId, Integer messageId, String content) throws TelegramApiException {
        return hookEditMessage(chatId, messageId, content, BuildVars.FORMAT_NONE);
    }

    /**
     * @param chatId
     * @param content
     * @param replyMessageId
     * @param textFormat     0-无格式化 1-格式化MarkDown 2-格式化Html
     * @return
     * @throws TelegramApiException
     */
    public Message hookSendMessage(String chatId, String content, Integer replyMessageId, Integer textFormat) throws TelegramApiException {
        SendMessage response = new SendMessage();

        if (textFormat == BuildVars.FORMAT_MARKDOWN)
            response.enableMarkdown(true);
        else if (textFormat == BuildVars.FORMAT_HTML)
            response.enableHtml(true);

        response.setText(content);
        response.setChatId(chatId);
        if (replyMessageId != 0) {
            response.setReplyToMessageId(replyMessageId);
        }
        return sendMessage(response);
    }

    /**
     * 编辑已发送的消息
     *
     * @param chatId
     * @param content
     * @param textFormat
     * @return
     * @throws TelegramApiException
     */
    public Message hookEditMessage(String chatId, Integer messageId, String content, Integer textFormat) throws TelegramApiException {
        EditMessageText response = new EditMessageText();

        if (textFormat == BuildVars.FORMAT_MARKDOWN)
            response.enableMarkdown(true);
        else if (textFormat == BuildVars.FORMAT_HTML)
            response.enableHtml(true);

        response.setText(content);
        response.setChatId(chatId);
        response.setMessageId(messageId);
        return editMessageText(response);
    }


    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
