package com.geosatis.schedules.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.util.DateConverter;

@Repository
public class ScheduleDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static String UPDATE_COLUMNS_PLACEHOLDER = "_UPDATE_COLUMNS_";

    private static final String SERIES_LIST = "series_list";

    public static final String SCHEDULE_ID = "schedule_id";

    public static final String START_DATE = "start_date";

    public static final String END_DATE = "end_date";

    public static final String IS_ACTIVE = "is_active";

    public static final String NAME = "name";

    private static final String CREATE_SCHEDULE = "INSERT INTO schedules(start_date, end_date, name, is_active)" +
            " VALUES (:start_date, :end_date, :name, :active)";

    private static final String FIND_SCHEDULE_BY_ID = "SELECT * FROM schedules WHERE schedule_id = :schedule_id";

    private static final String UPDATE_SCHEDULE_BY_ID = "UPDATE schedules SET " + UPDATE_COLUMNS_PLACEHOLDER + " WHERE schedule_id = :schedule_id ";

    private CascadeDao cascadeDao;

    private SeriesDao seriesDao;

    @Autowired
    public ScheduleDao(NamedParameterJdbcTemplate jdbcTemplate, CascadeDao cascadeDao, SeriesDao seriesDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.cascadeDao = cascadeDao;
        this.seriesDao = seriesDao;
    }

    public Long createSchedule(Schedule schedule) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

        sqlParameterSource.addValue("start_date", Timestamp.from(schedule.getStartDate()));
        sqlParameterSource.addValue("end_date", Timestamp.from(schedule.getEndDate()));
        sqlParameterSource.addValue("name", schedule.getName());
        sqlParameterSource.addValue("active", schedule.isActive());

        int rowsUpdated = jdbcTemplate.update(CREATE_SCHEDULE, sqlParameterSource, keyHolder);
        if (rowsUpdated == 1) {
            schedule.setScheduleId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        }
        return schedule.getScheduleId();
    }

    public Schedule findScheduleById(long scheduleId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(SCHEDULE_ID, scheduleId);
        return DataAccessUtils.singleResult(jdbcTemplate.query(FIND_SCHEDULE_BY_ID, mapSqlParameterSource, new ScheduleMapper()));
    }

    public boolean updateSchedule(long scheduleId, Map<String, Object> newFieldValues) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(SCHEDULE_ID, scheduleId);
        StringBuilder updateQueryStringBuilder = new StringBuilder();
        String fieldName;
        //Map<String, String> queries = new LinkedHashMap<>();
        List<QueryData> queries = new ArrayList<>();

        for (var entry : newFieldValues.entrySet()) {
            fieldName = entry.getKey();
            if (fieldName.equals(SERIES_LIST)) {
                try {
                    List<LinkedHashMap> seriesList = (List<LinkedHashMap>) entry.getValue();
                    seriesList.forEach(seriesMap -> {
                        queries.addAll(seriesDao.getUpdateSeriesQuery(seriesMap));
                    });
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            } else {
                updateQueryStringBuilder.append(fieldName).append("=:").append(fieldName).append(",");
                if (fieldName.equals(START_DATE) || fieldName.equals(END_DATE)) {
                    Timestamp date = DateConverter.getTimestamp(entry.getValue());
                    if (date != null) {
                        mapSqlParameterSource.addValue(entry.getKey(), date);
                    }
                } else {
                    mapSqlParameterSource.addValue(entry.getKey(), entry.getValue());
                }
            }
        }

        String scheduleUpdateQuery = UPDATE_SCHEDULE_BY_ID.replace(UPDATE_COLUMNS_PLACEHOLDER, updateQueryStringBuilder.toString().replaceAll(".$", ""));
        queries.add(new QueryData("schedules", scheduleUpdateQuery, mapSqlParameterSource));

        return cascadeDao.executeQueries(queries);
    }

    private static class ScheduleMapper implements RowMapper<Schedule> {

        @Override
        public Schedule mapRow(ResultSet rs, int i) throws SQLException {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(rs.getLong("schedule_id"));
            schedule.setName(rs.getString("name"));
            schedule.setStartDate(rs.getTimestamp("start_date").toInstant());
            schedule.setEndDate(rs.getTimestamp("end_date").toInstant());
            schedule.setActive(rs.getBoolean("is_active"));
            return schedule;
        }
    }
}