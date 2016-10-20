package com.loacg.kayo.entity;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/10/20 9:37
 */
public class Bind {

    private int id;
    private int type;
    private long chatId;
    private long userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Bind{" +
                "id=" + id +
                ", type=" + type +
                ", chatId=" + chatId +
                ", userId=" + userId +
                '}';
    }
}
