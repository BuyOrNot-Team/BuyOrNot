package com.basic.buyornot.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StringFormatter {
    public static String formatTimeAgo(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(pastTime, now);
        long minutes = ChronoUnit.MINUTES.between(pastTime, now);
        long hours   = ChronoUnit.HOURS.between(pastTime, now);
        long days    = ChronoUnit.DAYS.between(pastTime, now);
        long weeks   = days / 7;
        long months  = ChronoUnit.MONTHS.between(pastTime, now);
        long years   = ChronoUnit.YEARS.between(pastTime, now);

        if (seconds < 60)  return "방금 전";
        if (minutes < 60)  return minutes + "분 전";
        if (hours < 24)    return hours + "시간 전";
        if (days < 7)      return days + "일 전";
        if (weeks < 5)     return weeks + "주 전";
        if (months < 12)   return months + "개월 전";
        return years + "년 전";
    }

    public static String formatPrice(Integer price) {
        return "₩" + String.format("%,d", price);
    }
}
