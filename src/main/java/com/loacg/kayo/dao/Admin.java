package com.loacg.kayo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/11/2016 4:26 PM
 */
@Repository
public class Admin {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Integer> getAdminList() {
        List<Integer> admins = new ArrayList<>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT `userId` FROM `admin`");
        for (Map<String, Object> map : list) {
            admins.add(Integer.valueOf(map.get("userId").toString()));
        }
        return admins;
    }
}
