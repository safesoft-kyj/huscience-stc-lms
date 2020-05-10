package com.dtnsm.lms.report;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-28 오후 2:32
 * @desc Github : https://github.com/pub147
 **/
@Repository
@Qualifier
@RequiredArgsConstructor
public class TrainingLogReportRepository {

    private final JdbcTemplate jdbcTemplate;

    protected String query;

    /**
     * Employee log 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findLogByUser(String userId) {
        query = "select * from vw_training_log_summary where user_id like ? order by org_depart, org_team, user_id";
        return jdbcTemplate.queryForList(query, new Object[]{userId + '%'});
    }

    /**
     * Employee Log 상세 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findLogDetailByUser(String userId) {
        query = "select * from vw_training_log_detail where user_id like ? order by org_depart, org_team, user_id";
        return jdbcTemplate.queryForList(query, new Object[]{userId + '%'});
    }

    /**
     * 수료증 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findCertByUser(String userId) {
        query = "select * from vw_certificate_file_summary where user_id like ? order by org_depart, org_team, user_id";
        return jdbcTemplate.queryForList(query, new Object[]{userId + '%'});
    }

    /**
     * 수료증 상세 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findCertDetailByUser(String userId) {
        query = "select * from vw_certificate_file_detail where user_id like ? order by org_depart, org_team, user_id";
        return jdbcTemplate.queryForList(query, new Object[]{userId + '%'});
    }
}
