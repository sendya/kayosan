package com.loacg.controller;

import com.loacg.kayo.handlers.DirectionsHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/query")
    public @ResponseBody Object queryData() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM `t_video`");

        StringBuffer sb = new StringBuffer()
                .append(list.get(0).get("id").toString())
                .append("|")
                .append(list.get(0).get("short_code").toString())
                .append("|")
                .append(list.get(0).get("title").toString());

        return list;
    }

}
