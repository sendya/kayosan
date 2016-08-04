package com.loacg.service;

import com.loacg.kayo.Kayo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.updatesreceivers.BotSession;

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

    @RequestMapping("/query")
    public String queryData() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM users");
        System.out.println(list);



        return "ok";
    }
}
