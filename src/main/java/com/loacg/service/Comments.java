package com.loacg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/2/2016 12:38 AM
 */
@RestController
@RequestMapping("/comments")
public class Comments {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/typecho/{comment}")
    @ResponseBody
    public String typechoComment(HttpServletRequest request, @PathVariable() String comment) {

        Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM `users` WHERE `id`=1");
        System.out.println(data);

        return comment;
    }

}
