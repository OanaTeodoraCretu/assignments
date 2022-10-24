package com.geosatis.schedules.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static Timestamp getTimestamp(Object dateValue) {
        Date date = null;
        try {
            date = dateFormatter.parse((String) dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return Timestamp.from(date.toInstant());
        }
        return null;
    }


}
