package com.loacg.kayo.entity.duoshuo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.loacg.kayo.interfaces.IEntityObject;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 9/13/2016 12:05 AM
 */
public class DsMeta implements IEntityObject {

    private static final String POST_ID = "post_id";
    private static final String THREAD_ID = "thread_id";
    private static final String THREAD_KEY = "thread_key";
    private static final String AUTHOR_ID = "author_id";
    private static final String AUTHOR_KEY = "author_key";
    private static final String AUTHOR_NAME = "author_name";
    private static final String AUTHOR_EMAIL = "author_email";
    private static final String AUTHOR_URL = "author_url";
    private static final String IP = "ip";
    private static final String CREATE_AT = "created_at";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String TYPE = "type";
    private static final String PARENT_ID = "parent_id";
    private static final String AGENT = "agent";

    @JsonProperty(POST_ID)
    private Long postId;
    @JsonProperty(THREAD_ID)
    private Long threadId;
    @JsonProperty(THREAD_KEY)
    private String threadKey;
    @JsonProperty(AUTHOR_ID)
    private Integer authorId;
    @JsonProperty(AUTHOR_KEY)
    private String authorKey;
    @JsonProperty(AUTHOR_NAME)
    private String authorName;
    @JsonProperty(AUTHOR_EMAIL)
    private String authorEmail;
    @JsonProperty(AUTHOR_URL)
    private String authorUrl;
    @JsonProperty(IP)
    private String ip;
    @JsonProperty(CREATE_AT)
    private String createAt;
    @JsonProperty(MESSAGE)
    private String message;
    @JsonProperty(STATUS)
    private String status;
    @JsonProperty(TYPE)
    private String type;
    @JsonProperty(PARENT_ID)
    private Long parentId;
    @JsonProperty(AGENT)
    private String agent;

    public DsMeta() {
        super();
    }

    public DsMeta(JSONObject jsonObject) {
        super();
        this.postId = jsonObject.getLong(POST_ID);
        this.threadId = jsonObject.getLong(THREAD_ID);
        this.threadKey = jsonObject.getString(THREAD_KEY);
        this.authorId = jsonObject.getInt(AUTHOR_ID);
        this.authorKey = jsonObject.getString(AUTHOR_KEY);
        this.authorName = jsonObject.getString(AUTHOR_NAME);
        this.authorEmail = jsonObject.getString(AUTHOR_EMAIL);
        this.authorUrl = jsonObject.getString(AUTHOR_URL);
        this.ip = jsonObject.getString(IP);
        this.createAt = jsonObject.getString(CREATE_AT);
        this.message = jsonObject.getString(MESSAGE);
        this.status = jsonObject.getString(STATUS);
        this.type = jsonObject.getString(TYPE);
        if (jsonObject.get(PARENT_ID) != null && !"".equals(jsonObject.getString(PARENT_ID))) {
            this.parentId = jsonObject.getLong(PARENT_ID);
        }
        this.agent = jsonObject.getString(AGENT);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField(POST_ID, postId);
        gen.writeNumberField(THREAD_ID, threadId);
        gen.writeStringField(THREAD_KEY, threadKey);
        gen.writeNumberField(AUTHOR_ID, authorId);
        gen.writeStringField(AUTHOR_KEY, authorKey);
        gen.writeStringField(AUTHOR_NAME, authorName);
        gen.writeStringField(AUTHOR_EMAIL, authorEmail);
        gen.writeStringField(AUTHOR_URL, authorUrl);
        gen.writeStringField(IP, ip);
        gen.writeStringField(CREATE_AT, createAt);
        gen.writeStringField(MESSAGE, message);
        gen.writeStringField(STATUS, status);
        gen.writeStringField(TYPE, type);
        gen.writeNumberField(PARENT_ID, parentId);
        gen.writeStringField(AGENT, agent);
        gen.writeEndObject();
        gen.flush();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getThreadKey() {
        return threadKey;
    }

    public void setThreadKey(String threadKey) {
        this.threadKey = threadKey;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorKey() {
        return authorKey;
    }

    public void setAuthorKey(String authorKey) {
        this.authorKey = authorKey;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return "{" +
                "postId=" + postId +
                ", threadId=" + threadId +
                ", threadKey='" + threadKey + '\'' +
                ", authorId=" + authorId +
                ", authorKey='" + authorKey + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", ip='" + ip + '\'' +
                ", createAt='" + createAt + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                ", agent='" + agent + '\'' +
                '}';
    }
}
