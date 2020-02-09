package com.wds.util.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.function.BinaryOperator;

public class DateUtils {

    // s is yyyyMMdd
    public static Date dateFrom(String s) {
        return Date.from(LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate localDateFrom(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date dateFrom(LocalDate date) {
        Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static String formatDateFrom(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static LocalDate localDateFrom(String date, String format) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
    }

    public static String padDay(Integer day) {
        String sDay = (day < 10) ? "0"+day : String.valueOf(day);
        return sDay;
    }

    // use: MIN_DATE.apply(d1, d2)
    public static final BinaryOperator<LocalDate> MIN_DATE = BinaryOperator.minBy(Comparator.naturalOrder());
}
