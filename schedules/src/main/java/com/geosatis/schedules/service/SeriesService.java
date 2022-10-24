package com.geosatis.schedules.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.geosatis.schedules.dao.ExceptionDao;
import com.geosatis.schedules.dao.SeriesDao;
import com.geosatis.schedules.entities.QueryData;
import com.geosatis.schedules.entities.Schedule;
import com.geosatis.schedules.entities.Series;

@Service
public class SeriesService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final SeriesDao seriesDao;

    private final ExceptionDao exceptionDao;

    private static final String FIND_SERIES_BY_SCHEDULE_ID = "SELECT * FROM series WHERE schedule_id = :schedule_id";


    public SeriesService(NamedParameterJdbcTemplate jdbcTemplate, SeriesDao seriesDao, ExceptionDao exceptionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.seriesDao = seriesDao;
        this.exceptionDao = exceptionDao;
    }

    public List<Series> getSeriesByScheduleId(long scheduleId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("schedule_id", scheduleId);

        return jdbcTemplate.queryForList(FIND_SERIES_BY_SCHEDULE_ID, mapSqlParameterSource, Series.class);
    }

    public Boolean addNewExceptionToSeries(Schedule schedule, Series seriesInput) throws UnsupportedOperationException{
        // see which series is fulfilling all the conditions
        Series seriesToBeUpdated = schedule.getSeriesList().stream()
                .filter(currentSeries -> currentSeries.getFreqTypeId() == seriesInput.getFreqTypeId() &&
                        currentSeries.getFreqIntervalId() == seriesInput.getFreqIntervalId() &&
                        currentSeries.getRepeatIntervalValue() == seriesInput.getRepeatIntervalValue()).findFirst().get();
        if (seriesToBeUpdated.getExceptionId() != 0) {
            boolean shouldUpdateException = false;
            LinkedHashMap<String, Object> exceptionMap = new LinkedHashMap<>();
            //exception already defined for the series. check if dates are equal and if so, just change the start time and/or end time if any is different
            if (seriesToBeUpdated.getException().getForDate().equals(seriesInput.getException().getForDate())) {
                if (!seriesToBeUpdated.getException().getNewStartTime().equals(seriesInput.getException().getNewStartTime())) {
                    shouldUpdateException = true;
                    exceptionMap.put("new_start_time", seriesInput.getException().getNewStartTime());
                }
                if (!seriesToBeUpdated.getException().getNewEndTime().equals(seriesInput.getException().getNewEndTime())) {
                    exceptionMap.put("new_end_time", seriesInput.getException().getNewEndTime());
                    shouldUpdateException = true;
                }
                if (shouldUpdateException) {
                    exceptionMap.put("exception_id", seriesToBeUpdated.getException().getExceptionId());
                    QueryData queryData = exceptionDao.getUpdateExceptionQuery(exceptionMap);
                    jdbcTemplate.update(queryData.getQueryString(), queryData.getSqlParameters());
                }
            } else {
                // should allow multiple active exceptions per series? The code assumes not, but it makes sense to do so
                // for now just throw unsupported operation
                throw new UnsupportedOperationException();
            }

        } else {
            // create new exception for current series
            long exceptionId = exceptionDao.createException(seriesInput.getException());
            if (exceptionId > 0) {
                LinkedHashMap<String, Object> seriesMap = new LinkedHashMap<>();
                seriesMap.put("series_id", seriesToBeUpdated.getSeriesId());
                seriesMap.put("exception_id", exceptionId);
                QueryData query = seriesDao.getUpdateSeriesQuery(seriesMap).get(0);
                jdbcTemplate.update(query.getQueryString(), query.getSqlParameters());
            }
        }
        return true;
    }
}
