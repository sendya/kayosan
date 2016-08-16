package com.loacg.kayo.dao;

import com.loacg.kayo.entity.BotInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/12/2016 4:24 PM
 */
@Repository
public class BotInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(BotInfo bot) {
        if (this.get(bot.getK()) != null) {
            jdbcTemplate.update("UPDATE `bot_info` SET `v`=? WHERE `k`=?", new Object[]{bot.getV(), bot.getK()});
        } else {
            this.add(bot);
        }
    }

    public List<BotInfo> getList() {
        List<BotInfo> list = jdbcTemplate.query("SELECT `k`, `v` FROM `bot_info` ", new BeanPropertyRowMapper<>(BotInfo.class));
        return list;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> infos = new HashMap<>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT `k`, `v` FROM `bot_info` ");

        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            infos.put(map.get("k").toString(), map.get("v"));
        }
        return infos;
    }

    public BotInfo get(String k) {
        try {
            if (jdbcTemplate.queryForObject("SELECT count(*) FROM `bot_info` WHERE `k`=?", new Object[]{k}, Integer.class) > 0) {
                Map map = jdbcTemplate.queryForMap("SELECT `k`, `v` FROM `bot_info` WHERE `k`=?", new Object[]{k});
                if(map.size() > 0) {
                    return new BotInfo(map.get("k").toString(), map.get("v").toString()).build();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void add(BotInfo bot) {
        jdbcTemplate.update("INSERT INTO `bot_info` SET `k`=?, `v`=?", new Object[]{bot.getK(), bot.getV()});
    }

    public void remove(String k) {
        jdbcTemplate.update("DELETE FROM `bot_info` WHERE `k`=?", new Object[]{k});
    }

}
