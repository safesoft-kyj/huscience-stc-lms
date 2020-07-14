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
public class CourseReportRepository {

    private final JdbcTemplate jdbcTemplate;

    protected String query;

    /**
     * 수료증 상세 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findBySelfTraining(String courseTitle, String orgDepart, String orgTeam, String name, String status) {
        query = " select *" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and is_commit like ? ";
        return jdbcTemplate.queryForList(query
                , new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        , '%' + status + '%'
                    });
    }
}


