package com.loacg.kayo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/11/2016 3:42 PM
 */
@Repository
public class BindCommand {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getChatIds(Integer type) {
        List<Map<String, Object>> chatIds = jdbcTemplate.queryForList("SELECT `chatId` FROM `binds` WHERE `type`=?", new Object[]{type});
        return chatIds;
    }

    public boolean isBindCommand(Integer type, String charId) {
        int val = jdbcTemplate.queryForObject("SELECT count(*) FROM `binds` WHERE `type`=? AND `chatId`=?", new Object[]{type, Long.valueOf(charId)}, Integer.class);
        if (val > 0)
            return true;
        return false;
    }

    public void addBindCommand(String chatId, Integer type, String userId) {
        if (!this.isBindCommand(type, chatId)) {
            jdbcTemplate.update("INSERT INTO `binds` SET `type`=?, `chatId`=?, `userId`=?", new Object[]{type, Long.valueOf(chatId), Long.valueOf(userId)});
        }
    }

    public void removeBindCommand(String charId, Integer type) {
        jdbcTemplate.update("DELETE FROM `binds` WHERE `type`=? AND `chatId`=?", new Object[]{type, Long.valueOf(charId)});
    }

    public void removeBindAll() {
        jdbcTemplate.update("DELETE FROM `binds` WHERE 1=1");
    }

    public void removeBindAll(Integer type) {
        jdbcTemplate.update("DELETE FROM `binds` WHERE `type`=?", new Object[]{type});
    }


}
