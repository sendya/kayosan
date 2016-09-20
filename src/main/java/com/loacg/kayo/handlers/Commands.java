package com.loacg.kayo.handlers;

import com.loacg.kayo.dao.BotInfoDao;
import com.loacg.kayo.entity.BotInfo;
import com.loacg.kayo.entity.duoshuo.DsSiteInfo;
import com.loacg.utils.DecriptUtil;
import com.loacg.utils.HttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/9/12 15:34
 */
public class Commands {

    private static final Logger logger = LoggerFactory.getLogger(Commands.class);

    @Autowired private Directions directions;
    @Autowired private BotInfoDao botInfoDao;

    public void handler(Message message) {

        String text = message.getText();
        logger.info("User {}[{}] call command \"{}\" from \"{}\" , messageId: {}", message.getFrom().getUserName(), message.getFrom().getId(), text, message.getChatId(), message.getMessageId());

        if (message.getReplyToMessage().hasText()) {
            replyComment(message);
        }

        return;
    }

    public void bindSite(Message message) {



    }

    public void replyComment(Message message) {
        // \(#\S+\)
        if ("".equals(message.getReplyToMessage().getText())) {
            return;
        }
        String str = message.getReplyToMessage().getText();
        System.out.println(str);
        String pattern = "\\(#(\\S+)\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        List<String> list = new ArrayList<>();
        for(int i=0; i<2; ++i) {
            m.find();
            list.add(m.group(1));
        }



        String key = message.getChatId() + "_duoshuo_info";
        BotInfo dsInfo = botInfoDao.get(key);
        if (dsInfo == null || dsInfo.getV() == null || "".equals(dsInfo.getV().toString())) {
            try {
                directions.hookSendMessage(message.getChatId().toString(), "您未绑定留言功能。", message.getMessageId(), 0);
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        DsSiteInfo siteInfo = new DsSiteInfo(new JSONObject(dsInfo.getV().toString()));


        Map<String, Object> params = new HashMap<>();
        params.put("short_name", siteInfo.getShortName());
        params.put("secret", siteInfo.getSecret());
        params.put("thread_key", list.get(1));
        params.put("parent_id", list.get(0));
        params.put("remote_auth", DecriptUtil.JWSSign(siteInfo.getShortName(), siteInfo.getUserId().toString(), siteInfo.getSecret()));
        params.put("message", message.getText());
        logger.info("留言参数： {}", params);
        try {
            String jsonStr = HttpClient.post("http://api.duoshuo.com/posts/create.json", params);
            logger.info("留言结果： {}", jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
