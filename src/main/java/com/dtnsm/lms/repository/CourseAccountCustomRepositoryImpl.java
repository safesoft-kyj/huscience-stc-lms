package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.QCourseAccount;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.util.DateUtil;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.SQLExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;


public class CourseAccountCustomRepositoryImpl  extends QuerydslRepositorySupport implements CourseAccountCustomRepository  {

    public CourseAccountCustomRepositoryImpl() {
        super(CourseAccount.class);
    }

    @Override
    public List<CourseAccount> getCourseAccountByExpiration(String typeId, int dayCount) {
        final QCourseAccount courseAccount = QCourseAccount.courseAccount;

        return from(courseAccount)
                .where(SQLExpressions.datediff(DatePart.day, DateUtil.getStringToDate(courseAccount.toDate.toString()), DateExpression.currentDate()).eq(1)
                .and(courseAccount.courseStatus.notIn(CourseStepStatus.complete))
                .and(courseAccount.course.courseMaster.id.eq(typeId)))
                .orderBy(courseAccount.fromDate.asc())
                .fetch();
    }
}