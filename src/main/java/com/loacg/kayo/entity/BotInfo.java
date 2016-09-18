package com.loacg.kayo.entity;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/12/2016 4:29 PM
 */
public class BotInfo {

    private String k;
    private String v;

    public BotInfo() {
    }

    public BotInfo(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public BotInfo build() {
        return this;
    }

    public String getK() {
        return k;
    }

    public BotInfo setK(String k) {
        this.k = k;
        return this;
    }

    public Object getV() {
        return v;
    }

    public BotInfo setV(String v) {
        this.v = v;
        return this;
    }

    @Override
    public String toString() {
        return "BotInfo{" +
                "k='" + k + '\'' +
                ", v=" + v +
                '}';
    }
}
