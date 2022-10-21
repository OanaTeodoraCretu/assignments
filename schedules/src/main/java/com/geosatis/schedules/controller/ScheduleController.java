package com.geosatis.schedules.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.service.ScheduleService;

@RestController
@RequestMapping(path = "/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @PostMapping
    public ResponseEntity<Boolean> createNewSchedule(@RequestBody Schedule schedule) {
        // validate input
        return new ResponseEntity<>(scheduleService.createNewSchedule(schedule), HttpStatus.CREATED);
    }
}
