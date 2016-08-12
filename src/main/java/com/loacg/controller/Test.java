package com.loacg.controller;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @RequestMapping("/query/{name}")
    public @ResponseBody Object queryData(@PathVariable("name") String name) {

        bot.hookSendAudio(name, "-1001064858540", 0);

        return "success";
    }

    @RequestMapping("/send/voice")
    public @ResponseBody Object queryData() throws IOException {
        Resource resource = new ClassPathResource("audio/a00.m4a");
        bot.hookSendAudio(resource.getFile(), "-1001064858540", 0);

        return "success";
    }

}
