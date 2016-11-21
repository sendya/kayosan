package com.loacg.telegrambots.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * Project: kayosan
 * Author: liangliang.Yin <yinliangliang@rd.keytop.com.cn>
 * Date: 11/21/2016 9:39 AM
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface CommandMapping {

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    String[] command() default {};

    String[] params() default {};

    Permission[] permission() default {};

    MessageType[] messageType() default {};
}
