package com.loacg.entity;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/18/2016 9:03 AM
 */
public class Data {
    private static final long serialVersionUID = -8670328647557340122L;

    private int status = 200;
    private Object data;   // 返回数据
    private String message;    // 消息内容

    public static Data build() {
        return new Data();
    }

    public Data() {
        this.message = "Request failed";
        this.status = 200;
    }

    public Data(String message, int status) {
        if (message != null)
            this.message = message;
        this.status = status;
    }


    public int getStatus() {
        return status;
    }

    public Data setStatus(int status) {
        this.status = status;
        return this;
    }

    public Data setStatus(String message, int status) {
        this.message = message;
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Data setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Data setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Data{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

}
