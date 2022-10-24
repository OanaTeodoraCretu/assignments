package com.geosatis.schedules.entities;

import java.time.Instant;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Exception {

    @JsonProperty("exception_id")
    private long exceptionId;

    @JsonProperty("for_date")
    private Instant forDate;

    @JsonProperty("new_start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime newStartTime;

    @JsonProperty("new_end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime newEndTime;

    private boolean active;

    public long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(long exceptionId) {
        this.exceptionId = exceptionId;
    }

    public Instant getForDate() {
        return forDate;
    }

    public void setForDate(Instant forDate) {
        this.forDate = forDate;
    }

    public LocalTime getNewStartTime() {
        return newStartTime;
    }

    public void setNewStartTime(LocalTime newStartTime) {
        this.newStartTime = newStartTime;
    }

    public LocalTime getNewEndTime() {
        return newEndTime;
    }

    public void setNewEndTime(LocalTime newEndTime) {
        this.newEndTime = newEndTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
