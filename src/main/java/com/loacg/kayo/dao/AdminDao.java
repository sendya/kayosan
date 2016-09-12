package com.loacg.kayo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/11/2016 4:26 PM
 */
@Repository
public class AdminDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Integer> getAdminList() {
        List<Integer> admins = new ArrayList<>();
        jdbcTemplate.query("SELECT `userId` FROM `admin`", rs -> {
            admins.add(rs.getInt("userId"));
        });
        return admins;
    }
}
