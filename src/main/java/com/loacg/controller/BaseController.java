package com.loacg.controller;

import com.loacg.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/18/2016 10:16 AM
 */
@RestController
public class BaseController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    private final ErrorAttributes errorAttributes;

    @Autowired
    public BaseController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

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

    @RequestMapping(ERROR_PATH)
    @ResponseBody
    public Response handleError(HttpServletRequest aRequest) {
        Map<String, Object> body = getErrorAttributes(aRequest, getTraceParameter(aRequest));
        return Response.build().setStatus(body.get("message").toString(), Integer.valueOf(body.get("status").toString()));
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * error response | old
     *
     * @param aRequest
     * @return
     */
    public Map<String, Object> error(HttpServletRequest aRequest) {
        Map<String, Object> body = getErrorAttributes(aRequest, getTraceParameter(aRequest));
        String trace = (String) body.get("trace");
        if (trace != null) {
            String[] lines = trace.split("\n\t");
            body.put("trace", lines);
        }
        return body;
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}
