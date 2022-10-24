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
import com.geosatis.schedules.service.ZoneService;
import com.geosatis.schedules.util.DateConverter;

@RestController
@RequestMapping(path = "/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final SeriesService seriesService;

    private final ZoneService zoneService;

    public ScheduleController(ScheduleService scheduleService, SeriesService seriesService, ZoneService zoneService) {
        this.scheduleService = scheduleService;
        this.seriesService = seriesService;
        this.zoneService = zoneService;
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
            List<Schedule> scheduleList = scheduleService.getSchedulesByDate(date);
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

    @GetMapping("/checkPresence")
    public ResponseEntity<Boolean> shouldBeInTheZone(@RequestParam String date, @RequestParam long offenderId, @RequestParam long zoneId) {
        Timestamp specificDate = DateConverter.getTimestamp(date);
        if (specificDate == null) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        } else {
            boolean response = false;
            // check if the offender has access to the zone
            List<Long> offenderZones = zoneService.getOffenderZones(offenderId);
            if (offenderZones.stream().anyMatch(zoneIdForOffender -> zoneIdForOffender == zoneId)) {
                // get all schedules for the specified date
                List<Schedule> schedulesForSpecificDate = scheduleService.getSchedulesByDate(specificDate);
                // get all schedules ids for the zone where the offender is supposed to be
                List<Long> scheduleIdsForSpecificZone = zoneService.getScheduleIdsForZone(zoneId);
                //check to see if there is any exception defined for that schedule ( maybe usually the offender should be in the zone, but for some reason,
                // on a particular date, he/she shouldn't and we must check those exceptions as well
                if (!schedulesForSpecificDate.isEmpty() && !scheduleIdsForSpecificZone.isEmpty()) {
                    AtomicBoolean dateNotInException = new AtomicBoolean(true);
                    schedulesForSpecificDate.stream().filter(schedule -> scheduleIdsForSpecificZone.contains(schedule.getScheduleId()))
                            .forEach(schedule -> {
                                if (schedule.getSeriesList() != null) {
                                    schedule.getSeriesList().forEach(series -> {
                                        if (series.getException() != null && series.getException().getForDate().equals(specificDate.toInstant())) {
                                            dateNotInException.set(false);
                                        }
                                    });
                                }
                            });
                    if (dateNotInException.get()) {
                        response = true;
                    }
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
