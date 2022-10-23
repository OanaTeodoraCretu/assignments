package com.geosatis.schedules.entities;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class QueryData {
    private String tableName;

    private String queryString;

    private MapSqlParameterSource sqlParameters;

    public QueryData(String tableName, String queryString, MapSqlParameterSource sqlParameters) {
        this.tableName = tableName;
        this.queryString = queryString;
        this.sqlParameters = sqlParameters;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public MapSqlParameterSource getSqlParameters() {
        return sqlParameters;
    }

    public void setSqlParameters(MapSqlParameterSource sqlParameters) {
        this.sqlParameters = sqlParameters;
    }
}
