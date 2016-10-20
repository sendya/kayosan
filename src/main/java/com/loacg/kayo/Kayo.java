package com.loacg.kayo;

import com.loacg.kayo.handlers.Directions;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.updatesreceivers.BotSession;

/**
 * Project: kayo
 * Author: Sendya <18x@loacg.com>
 * Time: 8/1/2016 5:14 PM
 */
public class Kayo {

    public static BotSession botSession;

    @Autowired
    public static Directions directions;
}
