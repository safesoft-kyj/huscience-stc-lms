package com.dtnsm.lms.report;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<Map<String, Object>> findLogDetailByUser(String userName, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);

        String rowCountSql = "select count(*)"
                +" from vw_training_log_detail"
                +" where name like ?";

        int total = jdbcTemplate.queryForObject(rowCountSql, new Object[]{'%' + userName + '%'}, (rs, rowNum)-> rs.getInt(1));

        query = "select *"
                +" from vw_training_log_detail"
                +" where name like ?"
                +" order by org_depart, org_team, user_id"
                +" offset " + page + " * " + pageable.getPageSize() + " row"
                +" fetch next " + pageable.getPageSize() + " row only";

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(query, new Object[]{ '%' + userName + '%'});

        return new PageImpl<>(mapList, pageable, total);
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
    public Page<Map<String, Object>> findCertDetailByUser(String userName, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10);

        String rowCountSql = "select count(*) from vw_certificate_file_detail where name like ? ";

        int total = jdbcTemplate.queryForObject(rowCountSql, new Object[]{'%' + userName + '%'}
                , (rs, rowNum)-> rs.getInt(1));

        query = "select * from vw_certificate_file_detail"
                + " where name like ?"
                + " order by org_depart, org_team, user_id"
                + " offset " + page + " * " + pageable.getPageSize() + " row" +
                " fetch next " + pageable.getPageSize() + " row only";

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(query, new Object[]{ '%' + userName + '%'});

        return new PageImpl<>(mapList, pageable, total);
    }

    /**
     * 수료증 상세 조회
     * @param
     * @return
     * @exception
     * @see
     */
    public List<Map<String, Object>> findBindTargetList() {
        query = "select z.id, a.id as doc_id, z.title, a.user_id, u.name\n" +
                "\t, a.course_status\n" +
                "\t, d.status as section_status\n" +
                "\t, z.is_quiz\n" +
                "\t, isnull(c.id, 0) as quiz_action_id\n" +
                "\t, z.is_survey\n" +
                "\t, isnull(b.id, 0) as survey_action_id\n" +
                "\t, (select max(end_date) from el_course_section_action_history where section_action_id = d.id) as section_complete_date\n" +
                "\t, z.type_id\n" +
                "from el_course_account a with(nolock)\n" +
                "\tleft outer join el_course z with(nolock)\n" +
                "\t\ton z.id = a.course_id\n" +
                "\tleft outer join el_course_survey_action b with(nolock)\n" +
                "\t\ton a.id = b.doc_id\n" +
                "\tleft outer join el_course_quiz_action c with(nolock)\n" +
                "\t\ton a.id = c.doc_id\n" +
                "\tleft outer join el_course_section_action d with(nolock)\n" +
                "\t\ton a.id = d.doc_id\n" +
                "\tleft outer join account u with(nolock)\n" +
                "\t\ton a.user_id = u.user_id\n" +
                "--where z.title like '%[MFDS]%'\n" +
                "where z.is_survey = 'Y'\n" +
                "--and b.id is null\n" +
                "and a.course_status <> 'complete'\n" +
                "--and z.type_id = 'BC0101'\n" +
                "and d.status = 'COMPLETE'\n" +
                "order by course_status";
        return jdbcTemplate.queryForList(query);
    }
}
