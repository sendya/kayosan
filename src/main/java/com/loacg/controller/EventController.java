package com.loacg.controller;

import com.loacg.entity.Response;
import com.loacg.kayo.BuildVars;
import com.loacg.kayo.dao.BindCommandDao;
import com.loacg.kayo.dao.BotInfoDao;
import com.loacg.kayo.entity.BotInfo;
import com.loacg.kayo.entity.duoshuo.DsComment;
import com.loacg.kayo.entity.duoshuo.DsMeta;
import com.loacg.kayo.entity.duoshuo.DsSiteInfo;
import com.loacg.kayo.handlers.Directions;
import com.loacg.utils.DecriptUtil;
import com.loacg.utils.HttpClient;
import com.loacg.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/18/2016 9:27 AM
 */
@RestController
@RequestMapping("/event")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private BotInfoDao botInfoDao;

    @Autowired
    private BindCommandDao bindCommandDao;

    @Autowired
    private Directions bot;

    @RequestMapping(value = "/action/{eventType}", method = RequestMethod.POST)
    @ResponseBody
    public Response action(@PathVariable("eventType") String eventType, String type, String message, Integer format) throws TelegramApiException {
        Response response = Response.build().setMessage("This event not support");
        if (format == null)
            format = 0;

        String[] args = type.split(".");

        if ("comment".equals(eventType)) {
            switch (type) {
                case "typecho":
                    bot.hookSendMessage("-123225778", "[测试推送消息]\n" + message, 0, format);
                    break;
                case "wordpress":

                    break;
                default:
                    break;
            }
            return response.setStatus(String.format("Request event type : %s", type), 0);
        }

        return response;
    }

    @RequestMapping(value = "/callback/{user}.json", method = RequestMethod.POST)
    @ResponseBody
    public Response callback(HttpServletRequest request, @PathVariable("user") String user, String action, String signature) {
        String key = user + "_duoshuo_info";
        BotInfo dsInfo = botInfoDao.get(key);
        if (dsInfo == null || dsInfo.getV() == null || "".equals(dsInfo.getV().toString()))
            return Response.build().setMessage("Not bind site key!");

        DsSiteInfo siteInfo = new DsSiteInfo(new JSONObject(dsInfo.getV().toString()));
        String signatureCreate = DecriptUtil.getSignature(buildParam(request.getParameterMap()).getBytes(), siteInfo.getSecret().getBytes());
        if (!signature.equals(signatureCreate)) {
            logger.info("来源不可信，源加密： {}, 传递加密: {}", signatureCreate, signature);
            return Response.build().setMessage("Signature is not available");
        }

        StringBuffer url = new StringBuffer()
                .append("http://api.duoshuo.com/log/list.json")
                .append("?short_name=")
                .append(siteInfo.getShortName())
                .append("&secret=")
                .append(siteInfo.getSecret())
                .append("&since_id=")
                .append(siteInfo.getSinceId())
                .append("&limit=2")
                .append("&order=desc");

        String jsonStr = HttpClient.get(url.toString());
        logger.info("Content [S: {}", jsonStr);
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray arr = jsonObject.getJSONArray("response");
        DsComment comment = new DsComment(arr.getJSONObject(0));
        // 忽略本人的回复账户 ID , 唉 先写死算了。
        if (6332337422025622273L == comment.getUserId() || 12479007L == comment.getUserId()) return Response.build();
        StringBuffer message = new StringBuffer();
        if (comment.getMeta() instanceof DsMeta) {
            DsMeta meta = comment.getMeta();

            message.append("<b>[新的博客留言]</b>\n")
                    .append(meta.getAuthorName())
                    .append(" 说(")
                    .append("<a href=\"")
                    .append(siteInfo.getSiteUrl())
                    .append("/").append(meta.getThreadKey()).append("/\">#")
                    .append(meta.getPostId()).append("</a>):\n")
                    .append(meta.getMessage())
                    .append("\n\n点击查看(<a href=\"")
                    .append(siteInfo.getSiteUrl() + "/" + meta.getThreadKey())
                    .append("/\">#")
                    .append(meta.getThreadKey())
                    .append(")</a>");

            List<Map<String, Object>> binds = bindCommandDao.getChatIdsByTypeAndUserId(5, Integer.valueOf(user));
            try {
                if (binds.size() > 0) {
                    for (Map<String, Object> chatIds : binds) {
                        String chatId = chatIds.get("chatId").toString();
                        bot.hookSendMessage(chatId, message.toString(), 0, BuildVars.FORMAT_HTML);
                    }
                } else {
                    bot.hookSendMessage(user, message.toString(), 0, BuildVars.FORMAT_HTML);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            siteInfo.setSinceId(comment.getLogId());
            botInfoDao.save(dsInfo.setV(JsonUtils.toJson(siteInfo)));

        }

        return Response.build();
    }

    private static String buildParam(Map<String, String[]> params) {
        Iterator<Map.Entry<String, String[]>> it = params.entrySet().iterator();
        StringBuffer queryString = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            if ("signature".equals(entry.getKey()))
                continue;
            String values[] = entry.getValue();
            for (String val : values) {
                queryString.append(entry.getKey()).append("=").append(val).append("&");
            }
        }
        return queryString.deleteCharAt(queryString.length() - 1).toString();
    }
}
