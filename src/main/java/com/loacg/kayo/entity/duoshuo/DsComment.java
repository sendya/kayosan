package com.loacg.kayo.entity.duoshuo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.loacg.kayo.interfaces.IEntityObject;
import org.json.JSONObject;
import sun.dc.pr.PRError;

import java.io.IOException;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 9/12/2016 11:50 PM
 */
public class DsComment implements IEntityObject {

    private static final String LOG_ID = "log_id";
    private static final String SITE_ID = "site_id";
    private static final String USER_ID = "user_Id";
    private static final String ACTION = "action";
    private static final String META = "meta";
    private static final String DATE = "date";

    private static final String ACTION_CREATE = "create"; // 创建评论
    private static final String ACTION_APPROVE = "approve"; // 通过评论
    private static final String ACTION_SPAM = "spam"; // 标记垃圾评论
    private static final String ACTION_DELETE = "delete"; // 删除评论
    private static final String ACTION_DELETE_FOREVER = "delete-forever"; // 彻底删除评论

    @JsonProperty(LOG_ID)
    private Long logId;
    @JsonProperty(SITE_ID)
    private Integer siteId;
    @JsonProperty(USER_ID)
    private Long userId;
    @JsonProperty(ACTION)
    private String action;
    @JsonProperty(META)
    private DsMeta meta;
    @JsonProperty(DATE)
    private Integer date;

    public DsComment() {
        super();
    }

    public DsComment(JSONObject jsonObject) {
        super();
        this.logId = jsonObject.getLong(LOG_ID);

    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }
}
