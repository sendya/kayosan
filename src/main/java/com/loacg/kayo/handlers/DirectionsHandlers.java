package com.loacg.kayo.handlers;

import com.loacg.kayo.BotConfig;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.BotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Project: kayo
 * Author: Sendya <18x@loacg.com>
 * Time: 8/1/2016 5:15 PM
 */
public class DirectionsHandlers extends TelegramLongPollingBot {
    // STATUS
    private static final int WATING_ORIGIN_STATUS = 0;
    private static final int WATING_DESTINY_STATUS = 1;
    private static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // proxy
    private static final BotOptions options;

    static {
        options = new BotOptions();
        options.setProxyHost("127.0.0.1");
        options.setProxyPort(1080);
    }

    public DirectionsHandlers() {
        // super(options);
    }

    @Override
    public String getBotToken() {
        return BotConfig.TOKEN_KAYO;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.USERNAME_KAYO;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        System.out.println(String.format("%s\tDEBUG", DF.format(new Date())));
        if (message != null && message.hasText()) {
            System.out.println(String.format("%s\tINFO --- [UID:%s]\t%s", DF.format(new Date()), message.getChatId(), message.getText()));
            if (message.getText().startsWith("/start") || message.getText().startsWith("/help")) {
                this.sendHelp(message);
            } else if (message.getText().startsWith("/bind")) {
                handlerBindCommand(message);
            }
        }
    }

    public Message hookSendMessage(String chatId, String content) {
        Message message = null;
        SendMessage request = new SendMessage();
        request.enableMarkdown(true);
        request.setText(content);
        request.setChatId(chatId);
        try {
            message = sendMessage(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return message;
    }

    public Message hookEditMessage(String chatId, Integer messageId, String text) {
        Message message = null;
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        try {
            message = editMessageText(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return message;
    }


    public void sendMsg() throws TelegramApiException {
        Message message = null;
        SendMessage request = new SendMessage();
        request.enableMarkdown(true);
        StringBuffer sb = new StringBuffer()
                .append("Testing..");
        request.setText(sb.toString());
        request.setChatId("-16593353");

        message = sendMessage(request);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId().toString());
        editMessageText.setMessageId(message.getMessageId());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        editMessageText.setText("OK!!");

        editMessageText(editMessageText);

    }

    private void sendHelp(Message message) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);

        StringBuffer sb = new StringBuffer()
                .append("<code>[Command List]</code>:\n\n")
                .append("/help - Show Commands\n")
                .append("/ping - Kayo is online?\n")
                .append("/whoami - Show your Telegram ID\n\n")
                .append("Contact me via @Sendya\n\n")
                .append("Thanks for using Kayo Bot!");

        sendMessageRequest.setText(sb.toString());
        sendMessageRequest.setChatId(message.getChatId().toString());

        try {
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handlerBindCommand(Message message) {
        SendMessage request = new SendMessage();
        request.enableHtml(true);

        StringBuffer sb = new StringBuffer();

        String[] strs = message.getText().split(" ");
        if (strs.length >= 2) {
            if ("typecho_comment".equals(strs[1])) {
                sb.append("无权使用");
            } else if ("wordpress_comment".equals(strs[1])) {
                sb.append("无权使用");
            } else if ("netease_music".equals(strs[1])) {
                if(strs.length != 3) {
                    sb.append("格式错误。 /bind netease_music [你的cookie]");
                } else {
                    sb.append("无权使用");
                }
            } else if("qq_msg".equals(strs[1])) {
                if(strs.length != 3) {
                    sb.append("格式错误。 /bind qq_msg [QQ号]");
                } else {
                    sb.append("内测中, 您无权使用");
                }
            }
        }
        request.setText(sb.toString());
        request.setChatId(message.getChatId().toString());
        request.setReplyToMessageId(message.getMessageId().intValue());

        try {
            sendMessage(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
