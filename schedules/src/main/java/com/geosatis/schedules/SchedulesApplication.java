package com.geosatis.schedules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SchedulesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulesApplication.class, args);
    }


}

