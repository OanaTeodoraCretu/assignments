package com.geosatis.schedules.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.geosatis.schedules.entities.QueryData;

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
    boolean executeQueries(List<QueryData> queryDataList) {
        AtomicBoolean executedSuccessfully = new AtomicBoolean(true);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);
        queryDataList.forEach(queryData -> {
            try {
                int updatedRows = namedParameterJdbcTemplate.update(queryData.getQueryString(), queryData.getSqlParameters());
                // LOG.info("{} rows from table {} were updated", updatedRows, query.getTableName());
            } catch (Exception e) {
                dataSourceTransactionManager.rollback(status);
                executedSuccessfully.set(false);
            }
        });

        dataSourceTransactionManager.commit(status);
        return executedSuccessfully.get();
    }
}
