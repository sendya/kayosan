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
 * Time: 2016/9/14 10:20
 */
public class DsSiteInfo implements IEntityObject {

    private static final String SHORT_NAME = "short_name";
    private static final String SECRET = "secret";
    private static final String SINCE_ID = "since_id";

    private static final String SITE_URL = "site_url";

    @JsonProperty(SHORT_NAME)
    private String shortName;
    @JsonProperty(SECRET)
    private String secret;
    @JsonProperty(SINCE_ID)
    private Long sinceId;
    @JsonProperty(SITE_URL)
    private String siteUrl;

    public DsSiteInfo() {
        super();
    }

    public DsSiteInfo(JSONObject jsonObject) {
        super();
        this.shortName = jsonObject.getString(SHORT_NAME);
        this.secret = jsonObject.getString(SECRET);
        this.sinceId = jsonObject.getLong(SINCE_ID);
        this.siteUrl = jsonObject.getString(SITE_URL);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(SHORT_NAME, shortName);
        gen.writeStringField(SECRET, secret);
        gen.writeNumberField(SINCE_ID, sinceId);
        gen.writeStringField(SITE_URL, siteUrl);
        gen.writeEndObject();
        gen.flush();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    @Override
    public String toString() {
        return "DsSiteInfo{" +
                "shortName='" + shortName + '\'' +
                ", secret='" + secret + '\'' +
                ", sinceId=" + sinceId +
                ", siteUrl='" + siteUrl + '\'' +
                '}';
    }
}
