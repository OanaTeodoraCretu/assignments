package com.geosatis.schedules.entities;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Series {

    private Long seriesId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime startHour;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime endHour;

    private long freqTypeId;

    private int freqIntervalId;

    private Exception exception;

    private long scheduleId;

    private int repeatIntervalValue;

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

    public int getFreqIntervalId() {
        return freqIntervalId;
    }

    public void setFreqIntervalId(int freqIntervalId) {
        this.freqIntervalId = freqIntervalId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getRepeatIntervalValue() {
        return repeatIntervalValue;
    }

    public void setRepeatIntervalValue(int repeatIntervalValue) {
        this.repeatIntervalValue = repeatIntervalValue;
    }
}
