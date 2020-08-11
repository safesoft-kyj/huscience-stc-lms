package com.dtnsm.lms.report;

import com.dtnsm.lms.domain.DTO.CourseSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
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

    public Page<CourseSimple> findCourseSimpleByPage(String typeId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);


        String rowCountSql = "SELECT count(1) AS row_count " +
                "FROM vw_course_simple " +
                "WHERE type_id = ? ";
        int total =
                jdbcTemplate.queryForObject(
                        rowCountSql,
                        new Object[]{typeId}, (rs, rowNum) -> rs.getInt(1)
                );

        String querySql = "select * \n" +
                "from vw_course_simple \n" +
                "WHERE type_id = ? \n" +
                "order by insert_dt desc \n" +
//                "offset " + pageable.getOffset() + " row \n" +
                "offset " + page + " * " + pageable.getPageSize() + " row\n" +
                "fetch next " + pageable.getPageSize() + " row only\n";

        List<CourseSimple> demos = jdbcTemplate.query(
                querySql,
                new Object[]{typeId}, (rs, rowNum) -> CourseSimple.builder()
                        .id(rs.getString("id"))
                        .title(rs.getString("title"))
                        .status(rs.getInt("status"))
                        .requestFromDate(rs.getString("request_from_date"))
                        .requestToDate(rs.getString("request_to_date"))
                        .fromDate(rs.getString("from_date"))
                        .toDate(rs.getString("to_date"))
                        .requestDatePrior(rs.getString("request_date_prior"))
                        .accountCnt(rs.getInt("account_cnt"))
                        .sectionCnt(rs.getInt("section_cnt"))
                        .quizCnt(rs.getInt("quiz_cnt"))
                        .surveyCnt(rs.getInt("survey_cnt"))
                        .active(rs.getInt("active"))
                        .isAlways(rs.getString("is_always"))
                        .typeId(rs.getString("type_id"))
                        .typeName(rs.getString("type_name"))
                        .courseType(rs.getString("course_type"))
                        .isCerti(rs.getString("is_certi"))
                        .isQuiz(rs.getString("is_quiz"))
                        .isSurvey(rs.getString("is_survey"))
                        .build()
        );

        return new PageImpl<>(demos, pageable, total);
    }
}


