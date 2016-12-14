package com.loacg.kayo;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/16/2016 8:57 PM
 */
public class BuildVars {

    public static final String VERSION = "v1.0.1214.1527";

    public static final Integer FORMAT_NONE = 0;
    public static final Integer FORMAT_MARKDOWN = 1;
    public static final Integer FORMAT_HTML = 2;
    public static final Integer COMMAND_TIME_OUT = 45;

    public enum BindType {
        HITOKOTO, CHIME, COMMENT_TYPECHO, COMMENT_WORDPRESS, COMMENT_DUOSHUO
    }

}
