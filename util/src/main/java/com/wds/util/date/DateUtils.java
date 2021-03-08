package com.wds.util.date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static java.lang.String.format;

public class DateUtils {

    private static Logger logger = LogManager.getLogger(DateUtils.class);

    public static final String simpleDateFormat = "yyyyMMdd";
    public static final String dashedDateFormat = "yyyy-MM-dd";
    public static final String dashedDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter simpleDateFormatter = DateTimeFormatter.ofPattern(simpleDateFormat);
    public static final DateTimeFormatter dashedDateFormatter = DateTimeFormatter.ofPattern(dashedDateFormat);
    public static final DateTimeFormatter dashedDateTimeFormatter = DateTimeFormatter.ofPattern(dashedDateTimeFormat);

    /**
     * attempt to parse a string date
     * @param s
     * @return an Optional of LocalDate or Optional.empty() if s param is not formatted as yyyy-MM-dd
     */
    public static Optional<LocalDate> parseDate(String s) {
        try {
            return Optional.of(LocalDate.parse(s));
        } catch (Exception e) {
            logger.error(format("Failed parsing date %s! Expected format was: %s", s, dashedDateFormat));
            return Optional.empty();
        }
    }

    /**
     * attempt to parse an integer date
     * @param i
     * @return an Optional of LocalDate or Optional.empty() if s param is not formatted as yyyyMMdd
     */
    public static Optional<LocalDate> parseDateFromInt(Integer i) {
        try {
            return Optional.of(LocalDate.parse(String.valueOf(i), simpleDateFormatter));
        } catch (Exception e) {
            logger.error(format("Failed parsing date %d! Expected format was: %s", i, simpleDateFormat));
            return Optional.empty();
        }

    }

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

    /**
     * logs duration in seconds between start and current
     * @param start
     * @param stepDescription
     * @return current as Instant
     */
    public static Instant logDuration(Instant start, String stepDescription) {
        Instant current = Instant.now();
        logger.info(format("%d seconds ==> %s", Duration.between(start, current).toSeconds()), stepDescription);
        return current;
    }
}
