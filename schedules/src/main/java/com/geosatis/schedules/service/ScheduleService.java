package com.geosatis.schedules.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geosatis.schedules.dao.ExceptionDao;
import com.geosatis.schedules.dao.ScheduleDao;
import com.geosatis.schedules.dao.SeriesDao;
import com.geosatis.schedules.entities.Exception;
import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.entities.Series;

@Service
public class ScheduleService {

    private final ScheduleDao scheduleDao;

    private final SeriesDao seriesDao;

    private final ExceptionDao exceptionDao;

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, SeriesDao seriesDao, ExceptionDao exceptionDao) {
        this.scheduleDao = scheduleDao;
        this.seriesDao = seriesDao;
        this.exceptionDao = exceptionDao;
    }

    public boolean createNewSchedule(Schedule schedule) {
        Long scheduleId = scheduleDao.createSchedule(schedule);
        schedule.getSeriesList().forEach(series -> {
            if (scheduleId != null) {
                series.setScheduleId(scheduleId);
            }
            Exception exception = series.getException();
            if (exception != null) {
                long exceptionId = exceptionDao.createException(exception);
                if (exceptionId > 0) {
                    exception.setExceptionId(exceptionId);
                    series.setExceptionId(exceptionId);
                }
            }
            seriesDao.createSeries(series);
        });
        return true;
    }

    public boolean updateSchedule(Long scheduleId, Map<String, Object> newFieldValues) {
        //checkInputs(scheduleId, newFieldValues);
        return scheduleDao.updateSchedule(scheduleId, newFieldValues);
    }

    public List<Schedule> getSchedulesByDate(Timestamp date) {
        List<Schedule> scheduleList = scheduleDao.getSchedulesIdByDate(date);
        scheduleList.forEach(schedule -> {
            List<Series> seriesForASchedule = seriesDao.getSeriesForScheduleId(schedule.getScheduleId());
            seriesForASchedule.forEach(series -> {
                if (series.getExceptionId() > 0) {
                    series.setException(exceptionDao.getExceptionForSeries(series.getExceptionId()));
                }
            });
            schedule.setSeriesList(seriesForASchedule);
        });
        return scheduleList;
    }
}
