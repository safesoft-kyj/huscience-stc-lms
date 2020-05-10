package com.dtnsm.lms.report;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-28 오후 3:12
 * @desc Github : https://github.com/pub147
 **/
@Repository
public class NamedParameterReportRepository extends TrainingLogReportRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public NamedParameterReportRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate);
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> findLogByUser(String userId) {

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("user_id",  userId + "%");

        query = "select * from vw_training_log_summary where user_id like :user_id";

        return namedParameterJdbcTemplate.queryForList(
                query,
                mapSqlParameterSource);
    }
}
