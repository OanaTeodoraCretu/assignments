package com.geosatis.schedules.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.geosatis.schedules.dao.CascadeDao;
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

    private CascadeDao cascadeDao;

    private final static String SERIES_LIST =  "seriesList";

    @Autowired
    public ScheduleService(ScheduleDao scheduleDao, SeriesDao seriesDao, ExceptionDao exceptionDao, CascadeDao cascadeDao) {
        this.scheduleDao = scheduleDao;
        this.seriesDao = seriesDao;
        this.exceptionDao = exceptionDao;
        this.cascadeDao = cascadeDao;
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
}
