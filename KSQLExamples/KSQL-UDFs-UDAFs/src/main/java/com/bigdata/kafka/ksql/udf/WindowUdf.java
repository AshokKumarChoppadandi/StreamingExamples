package com.bigdata.kafka.ksql.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@UdfDescription(
        name = "get_window",
        description = "Create a Time window based on the given minutes",
        author = "Ashok Kumar Choppadandi",
        version = "1.0"
)
public class WindowUdf {

    // public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Udf(description = "Create a Time window when a timestamp value & window period provided")
    public String getWindow(
            @UdfParameter(value = "timestampInMilliseconds", description = "Value to get time window")
            final long timestampInMilliseconds,
            @UdfParameter(value = "timeWindow", description = "Time Window")
            final int timeWindow
    ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestampInMilliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minute = calendar.get(Calendar.MINUTE);

        int remainder = minute % timeWindow;
        int roundedMinutes = timeWindow - remainder;

        calendar.add(Calendar.MINUTE, roundedMinutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return dateFormat.format(calendar.getTime());
    }

    @Udf(description = "Create a Time window when a Date String & window period provided")
    public String getWindow(
            @UdfParameter(value = "timestamp", description = "Value to get time window")
            final String timestamp,
            @UdfParameter(value = "timeWindow", description = "Time Window")
            final int timeWindow
    ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minute = calendar.get(Calendar.MINUTE);

        int remainder = minute % timeWindow;
        int roundedMinutes = timeWindow - remainder;

        calendar.add(Calendar.MINUTE, roundedMinutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return dateFormat.format(calendar.getTime());
    }

}
