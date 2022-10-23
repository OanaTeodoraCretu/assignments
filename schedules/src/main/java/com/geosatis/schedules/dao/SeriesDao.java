package com.geosatis.schedules.dao;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.Exception;
import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.entities.Series;

@Repository
public class SeriesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static String UPDATE_COLUMNS_PLACEHOLDER = "_UPDATE_COLUMNS_";

    private static final String CREATE_SERIES = "INSERT INTO series(start_hour, end_hour, freq_type_id, freq_interval_id, schedule_id, exception_id, repeat_interval_value)" +
            " VALUES (:start_hour, :end_hour, :freq_type_id, :freq_interval_id, :schedule_id, :exception_id, :repeat_interval_value)";

    private static final String UPDATE_SERIES_BY_ID = "UPDATE series SET " + UPDATE_COLUMNS_PLACEHOLDER + " WHERE series_id = :series_id ";

    private ExceptionDao exceptionDao;
    @Autowired
    public SeriesDao(NamedParameterJdbcTemplate jdbcTemplate, ExceptionDao exceptionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.exceptionDao = exceptionDao;
    }

    public boolean createSeries(Series series) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("start_hour",  Time.valueOf(series.getStartHour()));
        sqlParameterSource.addValue("end_hour", Time.valueOf(series.getEndHour()));
        sqlParameterSource.addValue("freq_type_id", series.getFreqTypeId());
        sqlParameterSource.addValue("freq_interval_id", series.getFreqIntervalId());
        sqlParameterSource.addValue("schedule_id", series.getScheduleId());
        long exceptionId = 0;
        if (series.getException() != null) {
            exceptionId = series.getException().getExceptionId();
        }
        sqlParameterSource.addValue("exception_id", exceptionId);
        sqlParameterSource.addValue("repeat_interval_value", series.getRepeatIntervalValue());

        int rowsUpdated = jdbcTemplate.update(CREATE_SERIES, sqlParameterSource);

        return rowsUpdated == 1;
    }

    public List<QueryData> getUpdateSeriesQuery(LinkedHashMap<String, Object> seriesMap) {
        List<QueryData> queries = new ArrayList<>();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        StringBuilder updateQueryStringBuilder = new StringBuilder();
        String fieldName;
        for (var entry : seriesMap.entrySet()) {
            fieldName = entry.getKey();
            if (fieldName.equals("exception")) {
                queries.add(exceptionDao.getUpdateExceptionQuery((LinkedHashMap) entry.getValue()));
            } else {
                updateQueryStringBuilder.append(fieldName).append("=:").append(fieldName).append(",");
                mapSqlParameterSource.addValue(entry.getKey(), entry.getValue());
            }
        }

        queries.add(new QueryData("series", UPDATE_SERIES_BY_ID.replace(UPDATE_COLUMNS_PLACEHOLDER, updateQueryStringBuilder.toString().replaceAll(".$", "")), mapSqlParameterSource));
        return queries;
    }
}
