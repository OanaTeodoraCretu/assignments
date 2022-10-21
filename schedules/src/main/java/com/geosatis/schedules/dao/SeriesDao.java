package com.geosatis.schedules.dao;

import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.Series;

@Repository
public class SeriesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String CREATE_SERIES = "INSERT INTO series(start_hour, end_hour, freq_type_id, freq_interval, schedule_id, exception_id)" +
            " VALUES (:startHour, :endHour, :freqTypeId, :freqInterval, :scheduleId, :exceptionId)";

    @Autowired
    public SeriesDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean createSeries(Series series) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("startHour",  Time.valueOf(series.getStartHour()));
        sqlParameterSource.addValue("endHour", Time.valueOf(series.getEndHour()));
        sqlParameterSource.addValue("freqTypeId", series.getFreqTypeId());
        sqlParameterSource.addValue("freqInterval", series.getFreqInterval());
        sqlParameterSource.addValue("scheduleId", series.getScheduleId());
        sqlParameterSource.addValue("exceptionId", series.getExceptionId());

        int rowsUpdated = jdbcTemplate.update(CREATE_SERIES, sqlParameterSource);

        return rowsUpdated == 1;
    }
}
