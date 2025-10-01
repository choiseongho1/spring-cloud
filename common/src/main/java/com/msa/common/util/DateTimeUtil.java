package com.msa.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private DateTimeUtil() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }
    
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }
    
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static String formatTime(LocalTime time) {
        return formatTime(time, DEFAULT_TIME_FORMAT);
    }
    
    public static String formatTime(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_FORMAT);
    }
    
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMAT);
    }
    
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, DEFAULT_TIME_FORMAT);
    }
    
    public static LocalTime parseTime(String timeStr, String pattern) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_FORMAT);
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
