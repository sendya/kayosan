package com.loacg.controller;

import com.loacg.entity.Response;
import com.loacg.kayo.handlers.DirectionsHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.TelegramApiException;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/18/2016 9:27 AM
 */
@RestController
@RequestMapping("/event")
public class Event {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DirectionsHandlers bot;

    @RequestMapping(value = "/action/{eventType}", method = RequestMethod.POST)
    @ResponseBody
    public Response action(@PathVariable("eventType") String eventType, String type, String message, Integer format) throws TelegramApiException {
        Response response = Response.build().setMessage("This event not support");
        if (format == null)
            format = 0;

        String[] args = type.split(".");

        if ("comment".equals(eventType)) {
            switch (type) {
                case "typecho":
                    bot.hookSendMessage("-123225778", "[测试推送消息]\n" + message, 0, format);
                    break;
                case "wordpress":

                    break;
                default:
                    break;
            }
            return response.setStatus(String.format("Request event type : %s", type), 0);
        }

        return response;
    }
}
