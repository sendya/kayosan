package com.loacg.entity.duoshuo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.loacg.interfaces.IEntityObject;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 2016/10/20 9:38
 */
public class Comment implements IEntityObject {


    private static final String LOG_ID = "log_id";
    private static final String SITE_ID = "site_id";
    private static final String USER_ID = "user_id";
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
    private Meta meta;
    @JsonProperty(META)
    private Object metaObject;
    @JsonProperty(DATE)
    private Integer date;

    public Comment() {
        super();
    }

    public Comment(JSONObject jsonObject) {
        super();
        this.logId = jsonObject.getLong(LOG_ID);
        this.siteId = jsonObject.getInt(SITE_ID);
        this.userId = jsonObject.getLong(USER_ID);
        this.action = jsonObject.getString(ACTION);
        if (ACTION_CREATE.equals(this.action)) {
            this.meta = new Meta(jsonObject.getJSONObject(META));
        } else {
            this.metaObject = jsonObject.getJSONObject(META);
        }
        this.date = jsonObject.getInt(DATE);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField(LOG_ID, logId);
        gen.writeNumberField(SITE_ID, siteId);
        gen.writeNumberField(USER_ID, userId);
        gen.writeStringField(ACTION, action);
        if (ACTION_CREATE.equals(this.action)) {
            gen.writeObjectField(META, meta);
        } else {
            gen.writeObjectField(META, metaObject);
        }

        gen.writeNumberField(DATE, date);

        gen.writeEndObject();
        gen.flush();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Object getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(Object metaObject) {
        this.metaObject = metaObject;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DsComment{" +
                "logId=" + logId +
                ", siteId=" + siteId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", meta=" + meta +
                ", metaObject=" + metaObject +
                ", date=" + date +
                '}';
    }

}
