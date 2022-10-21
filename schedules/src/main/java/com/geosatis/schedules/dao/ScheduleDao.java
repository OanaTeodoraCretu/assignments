package com.geosatis.schedules.dao;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.Schedule;

@Repository
public class ScheduleDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String CREATE_SCHEDULE = "INSERT INTO schedules(start_date, end_date, name, is_active)" +
            " VALUES (:startDate, :endDate, :name, :active)";

    @Autowired
    public ScheduleDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createSchedule(Schedule schedule) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("startDate", schedule.getStartDate());
        sqlParameterSource.addValue("endDate", schedule.getEndDate());
        sqlParameterSource.addValue("name", schedule.getName());
        sqlParameterSource.addValue("active", schedule.isActive());

        int rowsUpdated = jdbcTemplate.update(CREATE_SCHEDULE, sqlParameterSource, keyHolder);
        if (rowsUpdated == 1) {
            schedule.setScheduleId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        }
        return schedule.getScheduleId();
    }

}
