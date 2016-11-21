package com.loacg.telegrambots.annotation;

import java.lang.annotation.*;

/**
 * Project: kayosan
 * Author: liangliang.Yin <yinliangliang@rd.keytop.com.cn>
 * Date: 11/21/2016 9:40 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {

    @AliasFor("attribute")
    String value() default "";

    @AliasFor("value")
    String attribute() default "";

    /**
     * The type of annotation in which the aliased {@link #attribute} is declared.
     * <p>Defaults to {@link Annotation}, implying that the aliased attribute is
     * declared in the same annotation as <em>this</em> attribute.
     */
    Class<? extends Annotation> annotation() default Annotation.class;
}
