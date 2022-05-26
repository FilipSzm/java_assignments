package uj.java.pwj2020.delegations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Calc {
    final static long millisecond = 1L;
    final static long second = 1000L * millisecond;
    final static long minute = 60L * second;
    final static long hour = 60L * minute;
    final static long day = 24L * hour;

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) {
        DateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        startFormat.setTimeZone(TimeZone.getTimeZone(start.split(" ")[2]));
        endFormat.setTimeZone(TimeZone.getTimeZone(end.split(" ")[2]));

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = startFormat.parse(start);
            endDate = endFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert endDate != null;
        long timeDifference = endDate.getTime() - startDate.getTime();
        if (timeDifference <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal numberOfDays = BigDecimal.ZERO;
        numberOfDays = numberOfDays.add(new BigDecimal(timeDifference / day));
        timeDifference = timeDifference % day;

        if (timeDifference == 0L) return numberOfDays.multiply(dailyRate);
        if (timeDifference / (12L * hour) > 0L) return numberOfDays.add(BigDecimal.ONE).multiply(dailyRate);
        if (timeDifference / (8L * hour) > 1L) return numberOfDays.multiply(dailyRate).add(dailyRate.divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
        return numberOfDays.multiply(dailyRate).add(dailyRate.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP));
    }
}
