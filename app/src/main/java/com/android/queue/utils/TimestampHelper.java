package com.android.queue.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimestampHelper {
    static public Timestamp datetimeToTimestamp(String datetime){
        return Timestamp.valueOf(datetime);
    }

    static public String toDatetime (Long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss");
        return simpleDateFormat.format(timestamp);
    }

    static public Timestamp TimestampDiff (Long before, Long after) {
        return new Timestamp(after - before);
    }

}
