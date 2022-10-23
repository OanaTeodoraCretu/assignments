package com.geosatis.schedules.entities;

import java.time.Instant;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Exception {

    private long exceptionId;

    private Instant forDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime newStartTime;

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
