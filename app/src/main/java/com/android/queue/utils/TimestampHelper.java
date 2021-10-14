package com.android.queue.utils;

import java.sql.Date;
import java.sql.Timestamp;

public class TimestampHelper {
    static public Timestamp datetimeToTimestamp(String datetime){
        return Timestamp.valueOf(datetime);
    }

    static public String toDatetime (Long timestamp) {
        Date date = new Date(timestamp);
        return date.toString();
    }
}
