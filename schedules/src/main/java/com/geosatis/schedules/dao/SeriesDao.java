package com.geosatis.schedules.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.entities.Series;

@Repository
public class SeriesDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static String UPDATE_COLUMNS_PLACEHOLDER = "_UPDATE_COLUMNS_";

    private static final String CREATE_SERIES = "INSERT INTO series(start_hour, end_hour, freq_type_id, freq_interval_id, schedule_id, exception_id, repeat_interval_value)" +
            " VALUES (:start_hour, :end_hour, :freq_type_id, :freq_interval_id, :schedule_id, :exception_id, :repeat_interval_value)";

    private static final String UPDATE_SERIES_BY_ID = "UPDATE series SET " + UPDATE_COLUMNS_PLACEHOLDER + " WHERE series_id = :series_id ";

    private static final String GET_SERIES_FOR_SCHEDULE_ID = "SELECT * FROM series WHERE schedule_id = :schedule_id ";

    private ExceptionDao exceptionDao;

    @Autowired
    public SeriesDao(NamedParameterJdbcTemplate jdbcTemplate, ExceptionDao exceptionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.exceptionDao = exceptionDao;
    }

    public boolean createSeries(Series series) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("start_hour", Time.valueOf(series.getStartHour()));
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
                mapSqlParameterSource.addValue(fieldName, entry.getValue());
            }
        }

        queries.add(new QueryData("series", UPDATE_SERIES_BY_ID.replace(UPDATE_COLUMNS_PLACEHOLDER, updateQueryStringBuilder.toString().replaceAll(".$", "")), mapSqlParameterSource));
        return queries;
    }

    public List<Series> getSeriesForScheduleId(long scheduleId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("schedule_id", scheduleId);

        return jdbcTemplate.query(GET_SERIES_FOR_SCHEDULE_ID, mapSqlParameterSource, new SeriesRSE());
    }

    private static class SeriesRSE implements ResultSetExtractor<List<Series>> {

        @Override
        public List<Series> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Series> seriesForASchedule = new ArrayList<>();

            while (rs.next()) {
                Series series = new Series();
                series.setSeriesId(rs.getLong(1));
                series.setStartHour(rs.getTimestamp(2).toLocalDateTime().toLocalTime());
                series.setEndHour(rs.getTimestamp(3).toLocalDateTime().toLocalTime());
                series.setFreqTypeId(rs.getInt(4));
                series.setFreqIntervalId(rs.getInt(5));
                series.setScheduleId(rs.getLong(6));
                series.setExceptionId(rs.getLong(7));
                series.setRepeatIntervalValue(rs.getInt(8));
                seriesForASchedule.add(series);
            }

            return seriesForASchedule;
        }
    }
}
