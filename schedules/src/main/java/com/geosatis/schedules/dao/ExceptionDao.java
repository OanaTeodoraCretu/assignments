package com.geosatis.schedules.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.Exception;
import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.util.DateConverter;

@Repository
public class ExceptionDao {

    private static final String FOR_DATE = "for_date";

    public static String UPDATE_COLUMNS_PLACEHOLDER = "_UPDATE_COLUMNS_";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String CREATE_EXCEPTION = "INSERT INTO exceptions(for_date, new_start_time, new_end_time, is_active)" +
            " VALUES (:for_date, :new_start_time, :new_end_time, :active)";

    private static final String UPDATE_EXCEPTION_BY_ID = "UPDATE exceptions SET " + UPDATE_COLUMNS_PLACEHOLDER + " WHERE exception_id = :exception_id";
    private static final String GET_EXCEPTION_BY_ID = "SELECT * FROM exceptions WHERE exception_id = :exception_id";


    @Autowired
    public ExceptionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createException(Exception exception) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("for_date", Timestamp.from(exception.getForDate()));
        sqlParameterSource.addValue("new_start_time", exception.getNewStartTime());
        sqlParameterSource.addValue("new_end_time", exception.getNewEndTime());
        sqlParameterSource.addValue("active", exception.isActive());

        int rowsUpdated = jdbcTemplate.update(CREATE_EXCEPTION, sqlParameterSource, keyHolder);
        if (rowsUpdated == 1) {
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        }
        return 0L;
    }

    public QueryData getUpdateExceptionQuery(LinkedHashMap<String, Object> exceptionMap) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        StringBuilder updateQueryStringBuilder = new StringBuilder();
        String fieldName;
        for (var entry : exceptionMap.entrySet()) {
            fieldName = entry.getKey();
            updateQueryStringBuilder.append(fieldName).append("=:").append(fieldName).append(",");
            if (fieldName.equals(FOR_DATE)) {
                Timestamp forDate = DateConverter.getTimestamp(entry.getValue());
                if (forDate != null) {
                    mapSqlParameterSource.addValue(entry.getKey(), forDate);
                }
            }
            mapSqlParameterSource.addValue(entry.getKey(), entry.getValue());
        }
        return new QueryData("exceptions", UPDATE_EXCEPTION_BY_ID.replace(UPDATE_COLUMNS_PLACEHOLDER, updateQueryStringBuilder.toString().replaceAll(".$", "")), mapSqlParameterSource);
    }

    public Exception getExceptionForSeries(long exceptionId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("exception_id", exceptionId);

        return jdbcTemplate.query(GET_EXCEPTION_BY_ID, mapSqlParameterSource, new ExceptionRSE());
    }

    private static class ExceptionRSE implements ResultSetExtractor<Exception> {
        @Override
        public Exception extractData(ResultSet rs) throws SQLException, DataAccessException {
            Exception exception = null;

            if (rs.next()) {
                exception = new Exception();
                exception.setExceptionId(rs.getLong(1));
                exception.setForDate(rs.getTimestamp(2).toInstant());
                exception.setNewStartTime(rs.getTimestamp(3).toLocalDateTime().toLocalTime());
                exception.setNewEndTime(rs.getTimestamp(4).toLocalDateTime().toLocalTime());
                exception.setActive(rs.getBoolean(5));
            }
            return exception;
        }
    }
}
