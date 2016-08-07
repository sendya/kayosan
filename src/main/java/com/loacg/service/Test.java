package com.loacg.service;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/4/2016 11:33 PM
 */
@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DirectionsHandlers bot;

    private static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @RequestMapping("/query")
    public String queryData() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM users");
        System.out.println(list);


        StringBuffer sb = new StringBuffer()
                .append(list.get(0).get("id").toString())
                .append("|")
                .append(list.get(0).get("username").toString())
                .append("|")
                .append(list.get(0).get("email").toString());

        return "ok";
    }

    @RequestMapping("/now")
    public String nowTime() {
        String time = DF.format(new Date());
        Message message = null;
        try {
            message = bot.hookSendMessage("-16593353", "**Kayo 报时** 现在时刻：" + time);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return message.getChatId() + "\t|\t" + message.getMessageId();
    }

    @RequestMapping("/upnow")
    public String upNowTime(String cid, String messageId) {
        String time = DF.format(new Date());
        try {
            bot.hookEditMessage(cid, Integer.valueOf(messageId), "**Kayo 报时** 现在时刻：" + time);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
