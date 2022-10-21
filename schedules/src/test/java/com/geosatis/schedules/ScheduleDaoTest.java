package com.geosatis.schedules;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.geosatis.schedules.dao.ScheduleDao;
import com.geosatis.schedules.entities.Schedule;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ScheduleDaoTest {

    @Autowired
    private ScheduleDao scheduleDao;

//    @Autowired
//    private JdbcTemplate template;
//
//    @Test
//    public void shouldInsertSchedule() {
//        Schedule schedule = new Schedule();
//        schedule.setStartDate(new Date());
//        schedule.setEndDate(new Date());
//        schedule.setName("Today");
//        schedule.setActive(true);
//
//       // boolean isInserted = scheduleDao.createSchedule(schedule);
//       // Assertions.assertEquals(true, isInserted);
//    }
}
