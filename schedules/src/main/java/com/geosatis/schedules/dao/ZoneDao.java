package com.geosatis.schedules.dao;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ZoneDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String GET_ALL_SCHEDULE_IDS_FOR_ZONE = "SELECT schedule_id FROM scheduleszones WHERE zone_id = :zone_id";

    private static final String GET_ALL_ZONE_IDS_FOR_OFFENDER = "SELECT zone_id FROM offenderszones WHERE offender_id = :offender_id";


    public ZoneDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> getScheduleIdsForZone(long zoneId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("zone_id", zoneId);

        return jdbcTemplate.queryForList(GET_ALL_SCHEDULE_IDS_FOR_ZONE, mapSqlParameterSource, Long.class);
    }

    public List<Long> getOffenderZones(long offenderId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("offender_id", offenderId);

        return jdbcTemplate.queryForList(GET_ALL_ZONE_IDS_FOR_OFFENDER, mapSqlParameterSource, Long.class);
    }

}
