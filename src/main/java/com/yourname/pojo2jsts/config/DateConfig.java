package com.yourname.pojo2jsts.config;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Date configuration for customizing date/time field generation
 */
public class DateConfig {
    private String format = "yyyy-MM-dd HH:mm:ss";
    private int pastDays = 365;     // 过去天数范围
    private int futureDays = 0;     // 未来天数范围
    private boolean useCurrentTime = false;  // 是否使用当前时间作为基准
    private String timeZone = "UTC";         // 时区设置
    
    public DateConfig() {}
    
    public DateConfig(String format, int pastDays, int futureDays) {
        this.format = format;
        this.pastDays = pastDays;
        this.futureDays = futureDays;
    }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { 
        this.format = format;
        // Validate format
        try {
            DateTimeFormatter.ofPattern(format);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format: " + format, e);
        }
    }
    
    public int getPastDays() { return pastDays; }
    public void setPastDays(int pastDays) { 
        if (pastDays < 0) {
            throw new IllegalArgumentException("Past days must be non-negative");
        }
        this.pastDays = pastDays; 
    }
    
    public int getFutureDays() { return futureDays; }
    public void setFutureDays(int futureDays) { 
        if (futureDays < 0) {
            throw new IllegalArgumentException("Future days must be non-negative");
        }
        this.futureDays = futureDays; 
    }
    
    public boolean isUseCurrentTime() { return useCurrentTime; }
    public void setUseCurrentTime(boolean useCurrentTime) { this.useCurrentTime = useCurrentTime; }
    
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    
    /**
     * Get total range in days (past + future)
     */
    public int getTotalRangeDays() {
        return pastDays + futureDays;
    }
    
    /**
     * Check if the date range includes future dates
     */
    public boolean includesFuture() {
        return futureDays > 0;
    }
    
    /**
     * Check if the date range includes past dates
     */
    public boolean includesPast() {
        return pastDays > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateConfig that = (DateConfig) o;
        return pastDays == that.pastDays &&
                futureDays == that.futureDays &&
                useCurrentTime == that.useCurrentTime &&
                Objects.equals(format, that.format) &&
                Objects.equals(timeZone, that.timeZone);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(format, pastDays, futureDays, useCurrentTime, timeZone);
    }
    
    @Override
    public String toString() {
        return "DateConfig{" +
                "format='" + format + '\'' +
                ", pastDays=" + pastDays +
                ", futureDays=" + futureDays +
                ", useCurrentTime=" + useCurrentTime +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}