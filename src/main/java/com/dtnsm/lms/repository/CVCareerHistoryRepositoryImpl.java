package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.QCVCareerHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.SQLExpressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CVCareerHistoryRepositoryImpl implements CVCareerHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public void find() {
        QCVCareerHistory cVCareerHistory = QCVCareerHistory.cVCareerHistory;
        List<Integer> resutls =
        queryFactory.select(SQLExpressions.datediff(DatePart.day, cVCareerHistory.startDate, cVCareerHistory.endDate))
        .from(cVCareerHistory)
        .fetch();
        log.info("@Results : {}", resutls);
//          queryFactory.select(Expressions.d)
//        .from(QCVCareerHistory.cVCareerHistory)

    }
}
