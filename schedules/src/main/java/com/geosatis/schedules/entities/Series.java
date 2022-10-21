package com.geosatis.schedules.entities;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geosatis.schedules.utils.TimeDeserializer;

public class Series {

    private Long seriesId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime startHour;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime endHour;

    private long freqTypeId;

    private int freqInterval;

    private long exceptionId;

    private long scheduleId;

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

    public long getFreqTypeId() {
        return freqTypeId;
    }

    public void setFreqTypeId(long freqTypeId) {
        this.freqTypeId = freqTypeId;
    }

    public int getFreqInterval() {
        return freqInterval;
    }

    public void setFreqInterval(int freqInterval) {
        this.freqInterval = freqInterval;
    }

    public long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(long exceptionId) {
        this.exceptionId = exceptionId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
