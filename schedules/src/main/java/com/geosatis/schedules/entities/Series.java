package com.geosatis.schedules.entities;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Series {

    @JsonProperty("series_id")
    private Long seriesId;

    @JsonProperty("start_hour")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime startHour;

    @JsonProperty("end_hour")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime endHour;

    @JsonProperty("freq_type_id")
    private long freqTypeId;

    @JsonProperty("freq_interval_id")
    private int freqIntervalId;

    @JsonProperty("exception_id")
    private long exceptionId;

    private Exception exception;

    @JsonProperty("schedule_id")
    private long scheduleId;

    @JsonProperty("repeat_interval_value")
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

    public long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(long exceptionId) {
        this.exceptionId = exceptionId;
    }
}
