package com.geosatis.schedules.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.geosatis.schedules.dao.ZoneDao;

@Service
public class ZoneService {

    private final ZoneDao zoneDao;

    public ZoneService(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    public List<Long> getScheduleIdsForZone(long zoneId) {
        return zoneDao.getScheduleIdsForZone(zoneId);
    }

    public List<Long> getOffenderZones(long offenderId) {
        return zoneDao.getOffenderZones(offenderId);
    }
}
