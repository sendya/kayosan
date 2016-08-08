package com.loacg.controller;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/8/2016 2:41 PM
 */
@RestController
@RequestMapping("/comment")
public class Comment {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private DirectionsHandlers bot;

    @RequestMapping("/typecho/{tid}")
    public @ResponseBody Object typecho(@PathVariable("tid") String tid, String message) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(message)) {
            message = "测试 API 投递消息";
        }
        try {
            bot.hookSendMessage(tid, message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
        map.put("status", 0);
        map.put("message", "success");
        return map;
    }
}
