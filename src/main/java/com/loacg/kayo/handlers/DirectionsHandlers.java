package com.loacg.kayo.handlers;

import com.loacg.kayo.BotConfig;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.BotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * Project: kayo
 * Author: Sendya <18x@loacg.com>
 * Time: 8/1/2016 5:15 PM
 */
public class DirectionsHandlers extends TelegramLongPollingBot {
    // STATUS
    private static final int WATING_ORIGIN_STATUS = 0;
    private static final int WATING_DESTINY_STATUS = 1;
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
        if (message != null && message.hasText()) {

            if (message.getText().startsWith("/start") || message.getText().startsWith("/help")) {
                this.sendHelp(message);
            } else if (message.getText().startsWith("/bind")) {
                handlerBindCommand(message);
            }
        }
    }

    private void sendHelp(Message message) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);

        StringBuffer sb = new StringBuffer()
                .append("<code>[Command List]</code>:\n\n")
                .append("/help - Show Commands\n")
                .append("/ping - Kayo is online?\n")
                .append("/whoami - Show your Telegram ID\n\n")
                .append("<code>[Netease Cloud Music API]</code>:\n\n")
                .append("/music_info [id] - Show music info\n")
                .append("/music_lrc [id] - Get music lyric\n")
                .append("/music_download [id] - Get mp3 file 320Kbps\n\n")
                .append("/bind [type] - 绑定命令\n")
                .append("   type\n\n")
                .append("   * typecho_comment - 绑定typecho博客即时提示/回复留言功能\n")
                .append("   * wordpress_comment - 绑定wordpress博客即时提示/回复留言功能\n")
                .append("   * netease_music 网易云音乐签到\n(格式: /bind [netease_music] COOKIE)\n")
                .append("   * qq_msg - 将消息推送给指定QQ号\n(格式：/bind [qq_msg] QQ号\n\n")
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
