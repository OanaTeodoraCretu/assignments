package com.geosatis.schedules.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geosatis.schedules.dao.ScheduleDao;
import com.geosatis.schedules.dao.SeriesDao;
import com.geosatis.schedules.entities.Schedule;

@Service
public class ScheduleService {

    private final ScheduleDao scheduleDao;

    private final SeriesDao seriesDao;

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, SeriesDao seriesDao) {
        this.scheduleDao = scheduleDao;
        this.seriesDao = seriesDao;
    }

    public boolean createNewSchedule(Schedule schedule) {
        Long scheduleId = scheduleDao.createSchedule(schedule);
        if (scheduleId != null) {
            schedule.getSeriesList().forEach(series -> {
                series.setScheduleId(scheduleId);
                seriesDao.createSeries(series);
            });
        }
        return true;
    }
}
