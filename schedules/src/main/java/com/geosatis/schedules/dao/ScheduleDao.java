package com.geosatis.schedules.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.entities.Series;
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

    private static final String FIND_SCHEDULE_BY_DATE = "SELECT * FROM schedules WHERE :specific_date BETWEEN start_date AND end_date";

    private static final String FIND_SCHEDULE_AND_CHILDREN_BY_DATE = " SELECT * FROM schedules AS sch LEFT JOIN " +
            "(SELECT series_id, start_hour, end_hour, freq_type_id, freq_interval_id, repeat_interval_value, schedule_id, ex.exception_id, for_date, new_start_time, new_end_time " +
            "FROM series as se LEFT JOIN exceptions as ex ON ex.exception_id = se.exception_id) as se ON se.schedule_id = sch.schedule_id " +
            "WHERE :specific_date BETWEEN start_date AND end_date ORDER BY sch.schedule_id, se.schedule_id; ";

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

    public List<Schedule> getSchedulesIdByDate(Timestamp date) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("specific_date", date);

        return jdbcTemplate.query(FIND_SCHEDULE_BY_DATE, mapSqlParameterSource, new ScheduleRSE());
    }

    public boolean updateSchedule(long scheduleId, Map<String, Object> newFieldValues) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(SCHEDULE_ID, scheduleId);
        StringBuilder updateQueryStringBuilder = new StringBuilder();
        String fieldName;
        List<QueryData> queries = new ArrayList<>();

        for (var entry : newFieldValues.entrySet()) {
            fieldName = entry.getKey();
            if (fieldName.equals(SERIES_LIST)) {
                try {
                    List<LinkedHashMap> seriesList = (List<LinkedHashMap>) entry.getValue();
                    seriesList.forEach(seriesMap -> queries.addAll(seriesDao.getUpdateSeriesQuery(seriesMap)));
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

    private static class ScheduleRSE implements ResultSetExtractor<List<Schedule>> {

        @Override
        public List<Schedule> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Schedule> results = new ArrayList<>();

            while (rs.next()) {
                results.add(new Schedule(rs.getLong(1), rs.getTimestamp(2).toInstant(),
                        rs.getTimestamp(3).toInstant(), rs.getString(4), rs.getBoolean(5)));
            }

            return results;
        }
    }
}