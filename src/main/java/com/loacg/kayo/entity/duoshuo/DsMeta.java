package com.loacg.kayo.entity.duoshuo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.loacg.kayo.interfaces.IEntityObject;

import java.io.IOException;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 9/13/2016 12:05 AM
 */
public class DsMeta implements IEntityObject {


    private Long postId;

    private Long threadId;

    private String threadKey;

    private Long authorId;

    private Long authorKey;

    private String authorName;

    private String authorEmail;

    private String authorUrl;

    private String ip;

    private String createAt;

    private String message;

    private String status;

    private String type;

    private Long parentId;

    private String agent;


    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }
}
