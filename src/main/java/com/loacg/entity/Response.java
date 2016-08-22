package com.loacg.entity;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/18/2016 9:03 AM
 */
public class Response {
    private static final long serialVersionUID = -8670328647557340122L;

    private int status = 200;
    private Object data;   // 返回数据
    private String message;    // 消息内容

    public static Response build() {
        return new Response();
    }

    public Response() {
        this.message = "Request failed";
        this.status = 200;
    }

    public Response(String message, int status) {
        if (message != null)
            this.message = message;
        this.status = status;
    }


    public int getStatus() {
        return status;
    }

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setStatus(String message, int status) {
        this.message = message;
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Response setData(Object data) {
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
