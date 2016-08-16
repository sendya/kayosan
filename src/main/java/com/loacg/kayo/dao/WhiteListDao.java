package com.loacg.kayo.dao;

import com.loacg.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/11/2016 4:16 PM
 */
@Repository
public class WhiteListDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addUser(Integer userId, Long chatId) {
        if (!this.isWhiteList(userId, chatId)) {
            int val = jdbcTemplate.update("INSERT INTO `white_list` SET `userId`=?, `chatId`=?, `add_time`=?", new Object[]{userId, chatId, DateUtil.getTimeStamp()});
            if (val > 0)
                return true;
        }
        return false;
    }

    public void removeUser(Integer userId) {
        jdbcTemplate.update("DELETE FROM `white_list` WHERE `userId`=?", new Object[]{userId});
    }

    public void removeUser(Integer userId, Long chatId) {
        jdbcTemplate.update("DELETE FROM `white_list` WHERE `userId`=? AND `chatId`=?", new Object[]{userId, chatId});
    }

    public boolean isWhiteList(Integer userId, Long chatId) {
        int val = jdbcTemplate.queryForObject("SELECT count(*) FROM `white_list` WHERE `userId`=? AND `chatId`=?", new Object[]{userId, chatId}, Integer.class);
        if (val > 0)
            return true;
        return false;
    }

}
