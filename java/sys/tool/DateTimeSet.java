package sys.tool;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期时间工具类（基于java.time API）
 */
public class DateTimeSet {
    // 预定义的格式化器（线程安全）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER_WITH_SECONDS =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前日期（格式：yyyy-MM-dd）
     */
    public static String dateSet() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间（格式：HH:mm:ss）
     */
    public static String timeSet() {
        return LocalTime.now().format(TIME_FORMATTER);
    }

    /**
     * 获取当前日期时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String dateTimeSet() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 获取当前年月日时分秒的数组
     */
    public static Integer[] now() {
        LocalDateTime now = LocalDateTime.now();
        return new Integer[]{
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute(),
                now.getSecond()
        };
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期时间格式错误，应为yyyy-MM-dd HH:mm:ss", e);
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 获取某年某月的天数
     */
    public static int getLastDay(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static String parseTime(String time) throws DateTimeParseException {
        LocalTime localTime = LocalTime.parse(time, TIME_FORMATTER_WITH_SECONDS);
        if(localTime.format(TIME_FORMATTER_WITH_SECONDS).length() < 8) {
            return localTime.format(TIME_FORMATTER_WITH_SECONDS) + ":00";
        }
        return localTime.format(TIME_FORMATTER_WITH_SECONDS); // 强制返回完整格式
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime dateOf(String dateTime) throws DateTimeParseException {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    /**
     * 根据年月日和时间字符串创建LocalDateTime
     */
    public static LocalDateTime dateOf(int year, int month, int day, String time) throws DateTimeParseException {
        LocalDate date = LocalDate.of(year, month, day);
        String formattedTime = parseTime(time); // 使用修正后的时间字符串
        LocalTime localTime = LocalTime.parse(formattedTime, TIME_FORMATTER_WITH_SECONDS);
        return LocalDateTime.of(date, localTime);
    }

    /**
     * 检查时间字符串格式是否正确（HH:mm:ss）
     */
    public static boolean checkTimeStr(String time) {
        try {
            LocalTime.parse(time, TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 获取两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static String formatTimeString(String time) {
        if (time != null && time.length() < 8) {
            // 补全小时部分为两位
            StringBuilder sb = new StringBuilder();
            String[] parts = time.split(":");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                if (parts[i].length() == 1) {
                    sb.append("0").append(parts[i]);
                } else {
                    sb.append(parts[i]);
                }
            }
            // 如果秒部分缺失，补全为 00
            while (sb.toString().split(":").length < 3) {
                sb.append(":00");
            }
            return sb.toString();
        }
        return time;
    }

    /**
     * 在日期上添加指定天数
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }
}