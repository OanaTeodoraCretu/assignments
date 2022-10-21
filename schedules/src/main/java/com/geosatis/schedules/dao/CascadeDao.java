package com.geosatis.schedules.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Repository
public class CascadeDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    public CascadeDao(DataSourceTransactionManager dataSourceTransactionManager) {
        DataSource dataSource = dataSourceTransactionManager.getDataSource();
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Transactional
    boolean executeQueries(Map<String, String> queries, MapSqlParameterSource mapSqlParameterSource) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);
        for (Map.Entry<String, String> query : queries.entrySet()) {
            try {
                int updatedRows = namedParameterJdbcTemplate.update(query.getValue(), mapSqlParameterSource);
            } catch (Exception e) {
                dataSourceTransactionManager.rollback(status);
                return false;
            }
        }

        dataSourceTransactionManager.commit(status);

        return true;
    }
}
