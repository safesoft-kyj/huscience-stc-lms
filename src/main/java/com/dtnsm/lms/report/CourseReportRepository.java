package com.dtnsm.lms.report;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.DTO.CourseAccountSimple;
import com.dtnsm.lms.domain.DTO.CourseSimple;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.domain.DTO.SelfTrainingList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UserServiceImpl userService;

    private final JdbcTemplate jdbcTemplate;

    protected String query;

    /**
     * 수료증 상세 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public Page<Map<String, Object>> findBySelfTraining(String courseTitle, String orgDepart, String orgTeam, String name, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);

        String rowCountSql = "select count(*)" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and is_commit like ? ";

        int total = jdbcTemplate.queryForObject(rowCountSql, new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        , '%' + status + '%'}
                , (rs, rowNum)-> rs.getInt(1));

        query = " select *" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and is_commit like ? "+
                " order by from_date desc" +
                " offset " + page + " * " + pageable.getPageSize() + " row" +
                " fetch next " + pageable.getPageSize() + " row only";

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(query
                , new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        , '%' + status + '%'
                });

        return new PageImpl<>(mapList, pageable, total);
    }

    public Page<Map<String, Object>> findByParentUser(String courseTitle, String orgDepart, String orgTeam, String name, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);

        String parentUser = userService.getAccountByUserId(SessionUtil.getUserId()).getUserId();

        String rowCountSql = "select count(*)" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and name in(select a.name FROM account as a where a.parent_user_id = ?)" +
                " and is_commit like ? ";

        int total = jdbcTemplate.queryForObject(rowCountSql, new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        ,  parentUser
                        , '%' + status + '%'}
                , (rs, rowNum)-> rs.getInt(1));

        query = " select *" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and name in(select a.name FROM account as a where a.parent_user_id = ?)" +
                " and is_commit like ? ";

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(query
                , new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        ,  parentUser
                        , '%' + status + '%'
                });

        return new PageImpl<>(mapList, pageable, total);
    }

    public Page<CourseSimple> findCourseSimpleByPage(String typeId, String title, String active, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);


        String rowCountSql = "select count(1) as row_count" +
                " from vw_course_simple" +
                " where type_id = ?" +
                " and title like ?" +
                " and active like ?" +
                " and status like ?";

        int total =
                jdbcTemplate.queryForObject(
                        rowCountSql,
                        new Object[]{typeId
                                , '%' + title + '%'
                                , active + '%'
                                , status + '%'
                        }
                        , (rs, rowNum) -> rs.getInt(1)
                );

        String querySql = "select * " +
                " from vw_course_simple" +
                " where type_id = ?" +
                " and title like ?" +
                " and active like ?" +
                " and status like ?" +
                " order by insert_dt desc" +
                " offset " + page + " * " + pageable.getPageSize() + " row" +
                " fetch next " + pageable.getPageSize() + " row only";

        List<CourseSimple> demos = jdbcTemplate.query(
                querySql,
                new Object[]{typeId
                        , '%' + title + '%'
                        , active + '%'
                        , status + '%'
                    }
                        , (rs, rowNum) -> CourseSimple.builder()
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

    /**
     *
     * @param
     * @return
     * @exception
     * @see
     */
    public List<CourseAccountSimple> findCourseAccountSimpleByPage(Long courseId) {

        String querySql = "select * \n" +
                "from vw_course_account_simple \n" +
                "WHERE course_id = ? \n" +
                "order by org_depart, name \n";


        List<CourseAccountSimple> courseAccounts = jdbcTemplate.query(
                querySql,
                new Object[]{courseId}, (rs, rowNum) -> CourseAccountSimple.builder()
                        .id(rs.getString("id"))
                        .orgDepart(rs.getString("org_depart"))
                        .name(rs.getString("name"))
                        .engName(rs.getString("eng_name"))
                        .fromDate(rs.getString("from_date"))
                        .toDate(rs.getString("to_date"))
                        .requestDate(rs.getString("request_date"))
                        .completeDate(rs.getString("complete_date"))
                        .requestType(rs.getString("request_type"))
                        .fnStatus(rs.getString("fn_status"))
                        .courseStatus(CourseStepStatus.valueOf(rs.getString("course_status")))
                        .isCommit(rs.getString("is_commit"))
                        .isAlways(rs.getString("is_always"))
                        .typeId(rs.getString("type_id"))
                        .isAttendance(rs.getString("is_attendance"))
                        .certificateBindDate(rs.getString("certificate_bind_date"))
                        .build()
        );
        return courseAccounts;
    }


    public List<SelfTrainingList> findBySelfTrainingAll(String courseTitle, String orgDepart, String orgTeam, String name, String status) {

        query = " select *" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and is_commit like ? ";

        List<SelfTrainingList> selfTrainingLists = jdbcTemplate.query(query
                , new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        , '%' + status + '%'}, (rs, rowNum) -> SelfTrainingList.builder()
                        .title(rs.getString("title"))
                        .orgDepart(rs.getString("org_depart"))
                        .orgTeam(rs.getString("org_team"))
                        .name(rs.getString("name"))
                        .fromDate(rs.getString("from_date"))
                        .toDate(rs.getString("to_date"))
                        .completeDate(rs.getString("log_apply_date"))
                        .courseStatus(CourseStepStatus.valueOf(rs.getString("course_status")).getLabel())
                        .isCommit(rs.getString("is_commit"))
                        .build()
        );
        return selfTrainingLists;
    }


    public List<SelfTrainingList> findByParentUserAll(String courseTitle, String orgDepart, String orgTeam, String name, String status) {

        String parentUser = userService.getAccountByUserId(SessionUtil.getUserId()).getUserId();

        query = " select *" +
                " from vw_course_self_report1" +
                " where title like ? " +
                " and org_depart like ? " +
                " and org_team like ? " +
                " and name like ? " +
                " and name in(select a.name FROM account as a where a.parent_user_id = ?)" +
                " and is_commit like ? ";

        List<SelfTrainingList> selfTrainingLists = jdbcTemplate.query(query
                , new Object[]{
                        '%' + courseTitle + '%'
                        , '%' + orgDepart + '%'
                        , '%' + orgTeam + '%'
                        , '%' + name + '%'
                        ,  parentUser
                        , '%' + status + '%'}, (rs, rowNum) -> SelfTrainingList.builder()
                        .title(rs.getString("title"))
                        .orgDepart(rs.getString("org_depart"))
                        .orgTeam(rs.getString("org_team"))
                        .name(rs.getString("name"))
                        .fromDate(rs.getString("from_date"))
                        .toDate(rs.getString("to_date"))
                        .completeDate(rs.getString("log_apply_date"))
                        .courseStatus(CourseStepStatus.valueOf(rs.getString("course_status")).getLabel())
                        .isCommit(rs.getString("is_commit"))
                        .build()
        );
        return selfTrainingLists;
    }
}


