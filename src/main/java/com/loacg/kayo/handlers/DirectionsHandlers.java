package com.loacg.kayo.handlers;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.dao.Admin;
import com.loacg.kayo.dao.BindCommand;
import com.loacg.kayo.dao.WhiteList;
import com.loacg.utils.DateUtil;
import com.loacg.utils.HttpClient;
import com.loacg.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVoice;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Project: kayo
 * Author: Sendya <18x@loacg.com>
 * Time: 8/1/2016 5:15 PM
 */
@Component
public class DirectionsHandlers extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;
    // STATUS
    private static final int WATING_ORIGIN_STATUS = 0;
    private static final int WATING_DESTINY_STATUS = 1;
    private static Logger logger = LoggerFactory.getLogger(DirectionsHandlers.class);
    private static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static List<Integer> adminList = new ArrayList<>();

    // Bind --> Chime on every hour
    private static List<String> chimeChatIds = new ArrayList<>();
    // Bind --> Hitokoto random message
    private static List<String> hitokotoChatIds = new ArrayList<>();

    private static List<Integer> whiteList = new ArrayList<>();
    // Robot start time
    private static long bootTime;

    private static boolean botStatus = true;

    @Autowired
    private BindCommand bindCommand;
    @Autowired
    private WhiteList whiteListDao;
    @Autowired
    private Admin adminDao;

    private Integer lastId = 0;

    public DirectionsHandlers() {
        bootTime = System.currentTimeMillis();
    }

    @PostConstruct
    public void start() {
        logger.info("Starting {} robot", botConfig.getName());

        List<Map<String, Object>> list = bindCommand.getChatIds(1);
        logger.info("Load bind chime {}", list.toString());
        for (Map<String, Object> map : list) {
            chimeChatIds.add(map.get("chatId").toString());
        }
        list = bindCommand.getChatIds(2);
        logger.info("Load bind hitokoto {}", list.toString());
        for (Map<String, Object> map : list) {
            hitokotoChatIds.add(map.get("chatId").toString());
        }
        adminList = adminDao.getAdminList();
        logger.info("Load admin {}", adminList.toString());
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        Integer time = Integer.valueOf(String.valueOf(DateUtil.getTimeStamp() - 60));

        if (message.getDate() < time) {
            logger.info("User {} call command timeout", message.getFrom().getId());
            return;
        }

        if (!botStatus || isAdmin(message.getFrom().getId())) { // 未开启服务将直接停止
            return;
        }

        logger.info(message.toString());
        if (message.getNewChatMember() != null && message.getNewChatMember().getId() != null) {
            try {
                String name = "";
                if (message.getNewChatMember().getLastName() != null)
                    name = message.getNewChatMember().getLastName();
                if (message.getNewChatMember().getFirstName() != null)
                    name += message.getNewChatMember().getFirstName();
                hookSendMessage(message.getChatId().toString(), String.format("热烈欢迎 `%s` 加入群组，请先查阅群置顶消息。", name), message.getMessageId());
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
            }
            return;
        }

        if (message.getLeftChatMember() != null && message.getLeftChatMember().getId() != null) {
            try {
                String name = "";
                if (message.getLeftChatMember().getLastName() != null)
                    name = message.getLeftChatMember().getLastName();
                if (message.getLeftChatMember().getFirstName() != null)
                    name += message.getLeftChatMember().getFirstName();
                hookSendMessage(message.getChatId().toString(), String.format("群成员 `%s` 离开了群组，-1s。", name), message.getMessageId());
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
            }
            return;
        }

        if (message != null && message.hasText()) {
            String text = message.getText();
            logger.info("User {}[{}] call command \"{}\" from \"{}\" , messageId: {}", message.getFrom().getUserName(), message.getFrom().getId(), text, message.getChatId(), message.getMessageId());

            try {
                if (text.startsWith("/start") || text.startsWith("/help")) {
                    this.handleSendHelp(message);
                } else if (text.startsWith("/ping")) {
                    handleSendPong(message);
                } else if (text.startsWith("/whoami")) {
                    handleSendWhoami(message);
                } else if (text.startsWith("/bind")) {
                    handleBindCommand(message);
                } else if (text.startsWith("/unbind")) {
                    handleBindCommand(message);
                } else if (text.startsWith("/whitelist")) {
                    handleAddWhiteList(message);
                } else if (text.startsWith("/control")) {
                    handleControlCommand(message);
                } else if (text.startsWith("/uptime")) {
                    handleSendUptime(message);
                } else if (text.startsWith("/test")) {
                    handleSendPhoto(message);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
        return hookSendMessage(chatId, content, 0);
    }

    public Message hookSendMessage(String chatId, String content, Integer replyMessageId) throws TelegramApiException {
        return hookSendMessage(chatId, content, replyMessageId, 1);
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
        Message message = null;
        SendMessage response = new SendMessage();
        switch (textFormat) {
            case 1:
                response.enableMarkdown(true);
                break;
            case 2:
                response.enableHtml(true);
                break;
            case 0:
            default:
                break;
        }
        response.setText(content);
        response.setChatId(chatId);
        if (replyMessageId != 0) {
            response.setReplyToMessageId(replyMessageId);
        }

        logger.info("Message: {}", response.getText());

        message = sendMessage(response);
        return message;
    }

    public Message hookEditMessage(String chatId, Integer messageId, String content) throws TelegramApiException {
        return hookEditMessage(chatId, messageId, content, 1);
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
        Message message = null;
        EditMessageText response = new EditMessageText();
        switch (textFormat) {
            case 1:
                response.enableMarkdown(true);
                break;
            case 2:
                response.enableHtml(true);
                break;
            case 0:
            default:
                break;
        }
        response.setText(content);
        response.setChatId(chatId);
        response.setMessageId(messageId);
        return editMessageText(response);
    }

    private void handleSendHelp(Message message) throws TelegramApiException {
        StringBuffer sb = new StringBuffer()
                .append("<code>[Command List]</code>:\n\n")
                .append("/help - Show commands\n")
                .append("/ping - Robot is online?\n")
                .append("/whoami - Show your Telegram ID\n\n")
                .append("/bind [command] - Bind delay Scheduled tasks\n")
                .append("/unbind [command] - Unbind delay Scheduled tasks\n")
                .append("/photo - Get random photo\n")
                .append("/uptime - Robot runtime\n")
                .append("Contact me via @Sendya\n\n")
                .append("Thanks for using @" + this.getBotUsername());

        hookSendMessage(message.getChatId().toString(), sb.toString(), 0, 2);
    }

    private void handleSendWhoami(Message message) throws TelegramApiException {
        StringBuffer sb = new StringBuffer();
        if (!message.isUserMessage()) {
            hookSendMessage(message.getChatId().toString(), sb.append("无法在群组或频道查看自己的 Telegram ID\n请私密 @").append(this.getBotUsername()).toString(), message.getMessageId(), 0);
        } else {
            hookSendMessage(message.getChatId().toString(), String.format("您的 Telegram ID 为 `%s`", message.getFrom().getId()), message.getMessageId(), 1);
        }
    }

    private void handleSendPong(Message message) throws TelegramApiException {
        hookSendMessage(message.getChatId().toString(), "Pong", message.getMessageId());
    }

    private void handleSendUptime(Message message) throws TelegramApiException {
        hookSendMessage(message.getChatId().toString(),
                String.format(this.getBotUsername() + " 已经运行了 %s", DateUtil.timeStampAutoShow(DateUtil.getTimeStamp(true) - bootTime)),
                message.getMessageId(), 0);
    }

    public void handleSendPhoto(Message message) throws TelegramApiException {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(message.getChatId().toString());
        //photo.setPhoto("AgADBQADqqcxG-ZUBw6vstLIeXj_r5XusTIABASiieMFwI6rkTUAAgI");
        // AgADBQADq6cxGyUSBw0ICEtTZSFvsP5eszIABGjCCqbNzDY6FjUAAgI
        // AgADBQADq6cxGyUSBw0ICEtTZSFvsP5eszIABCWV_bK0lpXPGDUAAgI
        // AgADBQADq6cxGyUSBw0ICEtTZSFvsP5eszIABChXXUwAAR6BtRc1AAIC
        // AgADBQADq6cxGyUSBw0ICEtTZSFvsP5eszIABEqxT9U7fDRwFTUAAgI
        photo.setPhoto("AgADBQADq6cxGyUSBw0ICEtTZSFvsP5eszIABChXXUwAAR6BtRc1AAIC");
        // photo.setNewPhoto(new File("D:\\Anime_pic\\001\\3.jpg"));
        photo.setCaption("你这个 Hentai ，/test 不是你想玩就能玩！\nSend photo tested..");
        Message message1 = sendPhoto(photo);

        System.out.println(message1);
    }

    private void handleControlCommand(Message message) throws TelegramApiException {
        if (isAdmin(message.getFrom().getId())) {
            hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        Message message1 = hookSendMessage(message.getChatId().toString(), "正在执行命令", message.getMessageId());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");
        if (command.length == 2) {
            if ("start".equals(command[1])) {
                botStatus = true;
                hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已启动完毕");
            } else if ("stop".equals(command[1])) {
                botStatus = false;
                hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已停止，除管理员启用将无法发送任何消息。");
            } else if ("restart".equals(command[1])) {
                botStatus = true;
                this.start();
                hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已重启完毕。");
            } else if ("reload".equals(command[1])) {
                this.start();
                hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已重载完毕。");
            }
        }
    }

    /**
     * 绑定来自 Telegram 的定时消息命令
     *
     * @param message
     */
    private void handleBindCommand(Message message) throws TelegramApiException {
        if (isAdmin(message.getFrom().getId())) {
            hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");
        if (command.length == 2) {

            if (command[0].startsWith("/bind")) {
                if ("hitokoto".equals(command[1])) {
                    if (hitokotoChatIds.contains(message.getChatId().toString()) == false) {
                        hitokotoChatIds.add(message.getChatId().toString());
                        bindCommand.addBindCommand(message.getChatId().toString(), 1, message.getFrom().getId().toString());
                    } else {
                        hookSendMessage(message.getChatId().toString(), String.format("已绑定过 `%s` 消息通知，无法重复绑定", command[1], message.getMessageId()));
                        return;
                    }
                } else if ("chime".equals(command[1])) {
                    if (chimeChatIds.contains(message.getChatId().toString()) == false) {
                        chimeChatIds.add(message.getChatId().toString());
                        bindCommand.addBindCommand(message.getChatId().toString(), 2, message.getFrom().getId().toString());
                    } else {
                        hookSendMessage(message.getChatId().toString(), String.format("已绑定过 `%s` 消息通知，无法重复绑定", command[1], message.getMessageId()));
                        return;
                    }
                }
                hookSendMessage(message.getChatId().toString(), String.format("已成功绑定 `%s` 消息通知", command[1]), message.getMessageId());
            } else if (command[0].startsWith("/unbind")) {
                if ("hitokoto".equals(command[1])) {
                    hitokotoChatIds.remove(message.getChatId().toString());
                    bindCommand.removeBindCommand(message.getChatId().toString(), 1);
                } else if ("chime".equals(command[1])) {
                    chimeChatIds.remove(message.getChatId().toString());
                    bindCommand.removeBindCommand(message.getChatId().toString(), 2);
                }
                hookSendMessage(message.getChatId().toString(), String.format("已取消绑定 `%s` 消息通知", command[1]), message.getMessageId());
            }

        } else {
            if (command[0].startsWith("/bind")) {
                hookSendMessage(message.getChatId().toString(), "Unknown command!!\nExample:\n\n/bind hitokoto - 在本群绑定一言\n/bind chime - 整点报时", message.getMessageId());
            } else {
                hookSendMessage(message.getChatId().toString(), "Unknown command!!\nExample:\n\n/unbind hitokoto - 取消绑定本群一言\n/unbind chime - 取消整点报时", message.getMessageId());
            }
        }
    }

    private void handleAddWhiteList(Message message) throws TelegramApiException {
        if (isAdmin(message.getFrom().getId())) {
            hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");
        if (command.length == 2) {
            if (whiteListDao.addUser(message.getFrom().getId(), message.getChatId())) {
                hookSendMessage(message.getChatId().toString(), "添加白名单成功", message.getMessageId());
            } else {
                hookSendMessage(message.getChatId().toString(), "添加白名单失败！！", message.getMessageId());
            }
        }
        return;
    }

    /**
     * 定时消息 一言（Hitokoto）
     */
    public void delaySendHitokoto() {
        logger.info("Tasks Hitokoto call , time {}", DateUtil.date2String(new Date()));

        String str = HttpClient.get("http://www.iqi7.com/hitokoto-now/api.php?encode=json&charset=utf8");
        if (StringUtils.isEmpty(str))
            return;

        Map<?, ?> map = JsonUtils.json2Map(str);
        String str2 = String.valueOf(map.get("hitokoto"));
        if (StringUtils.isEmpty(str2))
            return;
        try {
            for (String chatId : hitokotoChatIds) {
                hookSendMessage(chatId, str2);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时消息 整点报时（Chime）
     */
    public void delaySendChime() {
        logger.info("Tasks Chime call , time {}", DateUtil.date2String(new Date()));
        try {
            for (String chatId : chimeChatIds) {
                hookSendMessage(chatId, "*整点报时* 现在时间：" + DateUtil.date2String(new Date(), "yyyy-MM-dd HH:mm"));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时消息 早上问好，晚安（GoodMorning & Good bye）
     */
    public void delaySendGoodMorningAndBye() {
        logger.info("Tasks GoodMorning & Good bye call , time {}", DateUtil.date2String(new Date()));
        String text = null;
        SendVoice voice = new SendVoice();
        String nowTime = new SimpleDateFormat("HH:MM").format(new Date());
        int i = DateUtil.dateCompare(nowTime, "06:00", "HH:MM");
        try {
            if (i > 0) {
                // 不是早上
                //Resource resource = new ClassPathResource("audio/a00.m4a");
                //voice.setNewVoice(resource.getFile());
                voice.setVoice("AwADBQADFQAD5lQHDo0FTfui6P5nAg");
            } else {
                //Resource resource = new ClassPathResource("audio/a01.m4a");
                // voice.setNewVoice(resource.getFile());
                voice.setVoice("BQADBQADAQAD5lQHDgXZvgQWfPj9Ag");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (String chatId : chimeChatIds) {
                voice.setChatId(chatId);
                Message message = sendVoice(voice);
                logger.info(message.toString());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // 测试用代码
    public void hookSendAudio(String fileName, String chatId, Integer replyMessageId) {
        SendVoice voice = new SendVoice();
        try {
            // BQADBQADAQAD5lQHDgXZvgQWfPj9Ag    起来拉，一起吃早饭了
            // BQADBQADAgAD5lQHDs_SNMi7shzjAg    今天也要加油
            // BQADBQADBAAD5lQHDh4MZW5oIrxyAg
            // BQADBQADBQAD5lQHDrmbvZYxd-ilAg    H！

            Resource resource = new ClassPathResource(fileName);
            voice.setNewVoice(resource.getInputStream());
            voice.setChatId(chatId);
            if (replyMessageId != 0) {
                voice.setReplyToMessageId(replyMessageId);
            }

            Message message = sendVoice(voice);
            logger.info(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hookSendInlineKeyboard() {

    }

    private boolean isAdmin(Integer userId) {
        return !adminList.contains(userId);
    }

    private boolean isWhiteList(Long userId, Long chatId) {
        return false;
    }

}
