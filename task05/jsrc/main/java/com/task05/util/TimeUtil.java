package com.task05.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String now(){
        ZonedDateTime now = ZonedDateTime.now();
        String iso8601WithNanos = now.toOffsetDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
        return iso8601WithNanos;
    }
}
