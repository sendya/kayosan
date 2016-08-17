package com.loacg.kayo.handlers;

import com.loacg.kayo.BotConfig;
import com.loacg.kayo.BuildVars;
import com.loacg.kayo.dao.AdminDao;
import com.loacg.kayo.dao.BindCommandDao;
import com.loacg.kayo.dao.BotInfoDao;
import com.loacg.kayo.dao.WhiteListDao;
import com.loacg.kayo.entity.BotInfo;
import com.loacg.utils.DateUtil;
import com.loacg.utils.HttpClient;
import com.loacg.utils.JsonUtils;
import com.loacg.utils.SudoExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendVoice;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    // Robot white list
    private static List<Integer> whiteList = new ArrayList<>();
    // Robot info
    private static Map<String, Object> botInfo = new HashMap<>();

    // Robot start time
    private static long bootTime;

    private static boolean botStatus = true;

    @Autowired
    private BindCommandDao bindCommandDao;
    @Autowired
    private WhiteListDao whiteListDaoDao;
    @Autowired
    private AdminDao adminDaoDao;
    @Autowired
    private BotInfoDao botInfoDao;

    private static Integer locked = 0;

    public DirectionsHandlers() {
        bootTime = System.currentTimeMillis();
    }

    @PostConstruct
    public void start() {
        logger.info("Starting {} robot , version : {}", botConfig.getName(), BuildVars.VERSION);
        List<Map<String, Object>> list = bindCommandDao.getChatIds(1);
        logger.info("Load bind chime {}", list.toString());
        chimeChatIds.clear();
        for (Map<String, Object> map : list) {
            chimeChatIds.add(map.get("chatId").toString());
        }
        list = bindCommandDao.getChatIds(2);
        logger.info("Load bind hitokoto {}", list.toString());
        hitokotoChatIds.clear();
        for (Map<String, Object> map : list) {
            hitokotoChatIds.add(map.get("chatId").toString());
        }
        adminList.clear();
        adminList = adminDaoDao.getAdminList();
        logger.info("Load admin {}", adminList.toString());

        botInfo.clear();
        botInfo = botInfoDao.getMap();
        logger.info("Load bot info {}", botInfo.toString());
    }

    public void init() {
        if (botInfo.get("restart") != null && Integer.valueOf(botInfo.get("restart").toString()) == 1) {
            if (botInfo.get("last_message_id") != null && !"".equals(botInfo.get("last_message_id"))
                    && botInfo.get("last_chat_id") != null && !"".equals(botInfo.get("last_chat_id"))) {
                try {
                    logger.info("last_message_id {}", botInfo.get("last_message_id"));
                    this.hookEditMessage(botInfo.get("last_chat_id").toString(), Integer.valueOf(String.valueOf(botInfo.get("last_message_id"))), this.getBotUsername() + " 重启完毕。");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    botInfoDao.remove("restart");
                    botInfoDao.remove("last_message_id");
                    botInfoDao.remove("last_chat_id");
                    botInfo.clear();
                }
            }
        }
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

        Integer time = Integer.valueOf(String.valueOf(DateUtil.getTimeStamp() - 45));

        if (message.getDate() < time) {
            logger.info("User {} call command timeout", message.getFrom().getId());
            return;
        }

        if (!!!botStatus && !isAdmin(message.getFrom().getId())) { // 未开启服务将直接停止
            return;
        }

        logger.info(message.toString());

        this.handleWelcomeMessage(message);
        if (message != null && message.hasText()) {
            String text = message.getText();
            logger.info("User {}[{}] call command \"{}\" from \"{}\" , messageId: {}", message.getFrom().getUserName(), message.getFrom().getId(), text, message.getChatId(), message.getMessageId());

            try {
                if (text.startsWith("/start") || text.startsWith("/help")) {
                    this.handleSendHelp(message);
                    return;
                }
                if (text.startsWith("/ping")) {
                    this.handleSendPong(message);
                    return;
                }
                if (text.startsWith("/whoami")) {
                    this.handleSendWhoami(message);
                    return;
                }
                if (text.startsWith("/bind")) {
                    this.handleBindCommand(message);
                    return;
                }
                if (text.startsWith("/unbind")) {
                    this.handleBindCommand(message);
                    return;
                }
                if (text.startsWith("/whitelist")) {
                    this.handleAddWhiteList(message);
                    return;
                }
                if (text.startsWith("/control")) {
                    this.handleControlCommand(message);
                    return;
                }
                if (text.startsWith("/uptime")) {
                    this.handleSendUptime(message);
                    return;
                }
                if (text.startsWith("/rebuild")) {
                    this.handleRebuild(message);
                }
                if (text.startsWith("/test")) {
                    this.handleSendTest(message);
                    return;
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleWelcomeMessage(Message message) {
        if (message.getNewChatMember() != null && message.getNewChatMember().getId() != null) {
            try {
                String name = "";
                if (message.getNewChatMember().getLastName() != null)
                    name = message.getNewChatMember().getLastName();
                if (message.getNewChatMember().getFirstName() != null)
                    name += message.getNewChatMember().getFirstName();
                this.hookSendMessage(message.getChatId().toString(), String.format("热烈欢迎 `%s` 加入群组，请先查阅群置顶消息。", name), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
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
                .append("/rebuild - Build robot and restart\n")
                .append("Contact me via @Sendya\n\n")
                .append("Thanks for using @" + this.getBotUsername());

        this.hookSendMessage(message.getChatId().toString(), sb.toString(), 0, BuildVars.FORMAT_HTML);
    }

    private void handleSendWhoami(Message message) throws TelegramApiException {
        StringBuffer sb = new StringBuffer();
        if (!message.isUserMessage()) {
            this.hookSendMessage(message.getChatId().toString(), sb.append("无法在群组或频道查看自己的 Telegram ID\n请私密 @").append(this.getBotUsername()).toString(), message.getMessageId(), BuildVars.FORMAT_NONE);
            return;
        }
        this.hookSendMessage(message.getChatId().toString(), String.format("您的 Telegram ID 为 `%s`", message.getFrom().getId()), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
    }

    private void handleSendPong(Message message) throws TelegramApiException {
        this.hookSendMessage(message.getChatId().toString(), "Pong", message.getMessageId());
    }

    private void handleSendUptime(Message message) throws TelegramApiException {
        this.hookSendMessage(message.getChatId().toString(),
                String.format(this.getBotUsername() + " 已经运行了 %s", DateUtil.timeStampAutoShow(DateUtil.getTimeStamp(true) - bootTime)),
                message.getMessageId(), 0);
    }

    public void handleSendTest(Message message) throws TelegramApiException {
        if (!isAdmin(message.getFrom().getId())) {
            this.hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();

        InlineKeyboardButton openCommandLink = new InlineKeyboardButton();
        openCommandLink.setText("查看留言");
        openCommandLink.setUrl("http://www.baidu.com/");

        InlineKeyboardButton replyCommand = new InlineKeyboardButton();
        replyCommand.setText("回复留言");
        replyCommand.setUrl("http://www.baidu.com/");

        keyboardButtons.add(openCommandLink);
        keyboardButtons.add(replyCommand);

        keyboard.add(keyboardButtons);

        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        replyKeyboard.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setText("<b>博客留言：</b>\n" + "测试留言，测试测试~！！\n哈哈哈，测试个蛋（<a href=\"https://sendya.me/guestbook.html\">#307</a>）");
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage(sendMessage);
    }

    private void handleControlCommand(Message message) throws TelegramApiException {
        if (!isAdmin(message.getFrom().getId())) {
            this.hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        if (locked == 1) {
            if (!"".equals(botInfo.get("restart")) && !"".equals(botInfo.get("last_chat_id")) && !"".equals(botInfo.get("last_message_id"))) {
                this.hookEditMessage(botInfo.get("last_chat_id").toString(), Integer.valueOf(botInfo.get("last_message_id").toString()), this.getBotUsername() + " 正在处理重启中,请勿重复使其执行。");
                return;
            }
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");
        if (command.length == 2) {
            Message message1 = this.hookSendMessage(message.getChatId().toString(), "正在执行命令", message.getMessageId());
            if ("start".equals(command[1]) && !botStatus) {
                botStatus = true;
                this.hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已启动完毕");
                return;
            }
            if ("stop".equals(command[1]) && botStatus) {
                botStatus = false;
                this.hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已停止，除管理员启用将无法发送任何消息。");
                return;
            }
            if ("restart".equals(command[1])) {
                locked = 1;
                botInfoDao.save(new BotInfo("restart", "1").build());
                botInfoDao.save(new BotInfo("last_message_id", message1.getMessageId().toString()).build());
                botInfoDao.save(new BotInfo("last_chat_id", message1.getChatId().toString()).build());
                botInfo = botInfoDao.getMap(); // 更新设置一次数据
                this.hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 正在重启中。");
                try {
                    logger.info("Run SudoExecutor..");
                    SudoExecutor.run(SudoExecutor.buildCommands("/bin/bash /data/robot/update.sh"));
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if ("reload".equals(command[1])) {
                this.start();
                this.hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 已重载完毕。");
                return;
            }
        }
    }

    /**
     * 构建新版本 (临时)
     * @param message
     * @throws TelegramApiException
     */
    private void handleRebuild(Message message) throws TelegramApiException {
        if (!isAdmin(message.getFrom().getId())) {
            this.hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        if (locked == 1) {
            if (!"".equals(botInfo.get("restart")) && !"".equals(botInfo.get("last_chat_id")) && !"".equals(botInfo.get("last_message_id"))) {
                this.hookEditMessage(botInfo.get("last_chat_id").toString(), Integer.valueOf(botInfo.get("last_message_id").toString()), this.getBotUsername() + " 正在处理重启中,请勿重复使其执行。");
                return;
            }
        }
        Message message1 = this.hookSendMessage(message.getChatId().toString(), "正在执行命令", message.getMessageId());

        try {
            String result = null;
            // git pull
            this.hookEditMessage(message1.getChatId().toString(), message1.getMessageId(), "正在构建新代码");
            result = SudoExecutor.run(SudoExecutor.buildCommands("/data/robot/kayosan/build.sh"));
            logger.info(result);
            this.hookEditMessage(message1.getChatId().toString(), message1.getMessageId(), "正在进行清理工作，请稍等");
            result = SudoExecutor.run(SudoExecutor.buildCommands("/usr/bin/mv /data/robot/kayosan/build/libs/kayosan-1.0.1-SNAPSHOT.jar /data/robot/kayosan-1.0.1-SNAPSHOT.jar"));
            logger.info(result);

            // 构建完毕的程序移动到执行目录并且结束本进程，让 systemd 自动重启新程序
            locked = 1;
            botInfoDao.save(new BotInfo("restart", "1").build());
            botInfoDao.save(new BotInfo("last_message_id", message1.getMessageId().toString()).build());
            botInfoDao.save(new BotInfo("last_chat_id", message1.getChatId().toString()).build());
            botInfo = botInfoDao.getMap(); // 更新设置一次数据
            this.hookEditMessage(message.getChatId().toString(), message1.getMessageId(), this.getBotUsername() + " 正在重启中。");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定来自 Telegram 的定时消息命令
     *
     * @param message
     */
    private void handleBindCommand(Message message) throws TelegramApiException {
        if (!isAdmin(message.getFrom().getId())) {
            this.hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");

        if (command.length < 2) {
            if (command[0].startsWith("/bind")) {
                this.hookSendMessage(message.getChatId().toString(), "Unknown command!!\nExample:\n\n/bind hitokoto - 在本群绑定一言\n/bind chime - 整点报时", message.getMessageId());
                return;
            }
            this.hookSendMessage(message.getChatId().toString(), "Unknown command!!\nExample:\n\n/unbind hitokoto - 取消绑定本群一言\n/unbind chime - 取消整点报时", message.getMessageId());
            return;
        }

        if (command[0].startsWith("/bind")) {
            if ("hitokoto".equals(command[1])) {
                if (hitokotoChatIds.contains(message.getChatId().toString()) == false) {
                    hitokotoChatIds.add(message.getChatId().toString());
                    bindCommandDao.addBindCommand(message.getChatId().toString(), 1, message.getFrom().getId().toString());
                } else {
                    this.hookSendMessage(message.getChatId().toString(), String.format("已绑定过 `%s` 消息通知，无法重复绑定", command[1], message.getMessageId()), BuildVars.FORMAT_MARKDOWN);
                    return;
                }
            } else if ("chime".equals(command[1])) {
                if (chimeChatIds.contains(message.getChatId().toString()) == false) {
                    chimeChatIds.add(message.getChatId().toString());
                    bindCommandDao.addBindCommand(message.getChatId().toString(), 2, message.getFrom().getId().toString());
                } else {
                    this.hookSendMessage(message.getChatId().toString(), String.format("已绑定过 `%s` 消息通知，无法重复绑定", command[1], message.getMessageId()), BuildVars.FORMAT_MARKDOWN);
                    return;
                }
            }
            this.hookSendMessage(message.getChatId().toString(), String.format("已成功绑定 `%s` 消息通知", command[1]), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
            return;
        }

        if (command[0].startsWith("/unbind")) {
            if ("hitokoto".equals(command[1])) {
                hitokotoChatIds.remove(message.getChatId().toString());
                bindCommandDao.removeBindCommand(message.getChatId().toString(), 1);
            } else if ("chime".equals(command[1])) {
                chimeChatIds.remove(message.getChatId().toString());
                bindCommandDao.removeBindCommand(message.getChatId().toString(), 2);
            }
            this.hookSendMessage(message.getChatId().toString(), String.format("已取消绑定 `%s` 消息通知", command[1]), message.getMessageId(), BuildVars.FORMAT_MARKDOWN);
        }
    }

    private void handleAddWhiteList(Message message) throws TelegramApiException {
        if (!isAdmin(message.getFrom().getId())) {
            this.hookSendMessage(message.getChatId().toString(), "你不是管理员。", message.getMessageId());
            return;
        }
        String text = message.getText();
        String botName = "@" + this.getBotUsername();
        if (text.indexOf(botName) != -1) {
            text = text.replace(botName, "");
        }
        String command[] = text.split(" ");

        if (command.length < 2) {
            this.hookSendMessage(message.getChatId().toString(), "命令格式错误 /whitelist [ID]", message.getMessageId());
            return;
        }
        if (whiteListDaoDao.addUser(message.getFrom().getId(), message.getChatId())) {
            this.hookSendMessage(message.getChatId().toString(), "添加白名单成功", message.getMessageId());
            return;
        }
        this.hookSendMessage(message.getChatId().toString(), "添加白名单失败！！", message.getMessageId());
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
                this.hookSendMessage(chatId, str2);
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
                this.hookSendMessage(chatId, "*整点报时* 现在时间：" + DateUtil.date2String(new Date(), "yyyy-MM-dd HH:mm"));
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
                voice.setVoice("AwADBQADFwAD5lQHDrIYzhtM9BdyAg");
            } else {
                //Resource resource = new ClassPathResource("audio/a01.m4a");
                // voice.setNewVoice(resource.getFile());
                voice.setVoice("AwADBQADFgAD5lQHDl-UuIYqVEdYAg");
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
    public void hookSendAudio(File file, String chatId, Integer replyMessageId) {
        SendVoice voice = new SendVoice();
        try {
            voice.setNewVoice(file);
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

    // 测试用代码
    public void hookSendAudio(String fileName, String chatId, Integer replyMessageId) {
        SendVoice voice = new SendVoice();
        try {
            // Resource resource = new ClassPathResource(fileName);
            // voice.setNewVoice(resource.getInputStream());
            voice.setVoice(fileName);
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

    private boolean isAdmin(Integer userId) {
        return adminList.contains(userId);
    }

    private boolean isWhiteList(Long userId, Long chatId) {
        return false;
    }

    public static boolean getBotStatus() {
        return botStatus;
    }
}
