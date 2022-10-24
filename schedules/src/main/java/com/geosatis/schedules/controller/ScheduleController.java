package com.geosatis.schedules.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.entities.Series;
import com.geosatis.schedules.service.ScheduleService;
import com.geosatis.schedules.service.SeriesService;
import com.geosatis.schedules.util.DateConverter;

@RestController
@RequestMapping(path = "/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final SeriesService seriesService;

    public ScheduleController(ScheduleService scheduleService, SeriesService seriesService) {
        this.scheduleService = scheduleService;
        this.seriesService = seriesService;
    }

    @PostMapping
    public ResponseEntity<Boolean> createNewSchedule(@RequestBody Schedule schedule) {
        // validate input
        return new ResponseEntity<>(scheduleService.createNewSchedule(schedule), HttpStatus.CREATED);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Boolean> updateSchedule(@PathVariable("scheduleId") Long scheduleId, @RequestBody Map<String, Object> newFieldValues) {
        // validate input
        return new ResponseEntity<>(scheduleService.updateSchedule(scheduleId, newFieldValues), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateScheduleForSpecificDate(@RequestParam String specificDate, @RequestBody Series series) {
        Timestamp date = DateConverter.getTimestamp(specificDate);
        if (date != null) {
            List<Schedule> scheduleList = scheduleService.getSchedulesIdByDate(date);
            // assuming that there are multiple schedules contain the specific date, exception should apply to all series from all schedules
            AtomicBoolean success = new AtomicBoolean(true);
            scheduleList.forEach(schedule -> {
                try {
                    seriesService.addNewExceptionToSeries(schedule, series);
                } catch (UnsupportedOperationException ex) {
                    success.set(false);
                }
            });
            return new ResponseEntity<>(success.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
