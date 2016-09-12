package com.loacg.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/9/12 15:52
 */
@RestController
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "This page is not available";
    }

    @RequestMapping("/memory")
    public String status() {
        double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
        double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);
        double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);

        StringBuffer sb = new StringBuffer()
                .append("JVM 最大可用内存：")
                .append(max).append("MB<br/>")
                .append("当前占用内存：")
                .append(total).append("MB<br/>")
                .append("当前空闲内存：")
                .append(free).append("MB<br/>")
                .append("实际可用内存：")
                .append(max - total + free).append("MB");
        return sb.toString();
    }
}
