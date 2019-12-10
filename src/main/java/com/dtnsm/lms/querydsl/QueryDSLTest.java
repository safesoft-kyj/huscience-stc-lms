package com.dtnsm.lms.querydsl;

import com.dtnsm.lms.domain.CourseAccount;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class QueryDSLTest extends QuerydslRepositorySupport {

    @PersistenceContext
    private EntityManager em;

    public QueryDSLTest() {
        super(CourseAccount.class);
    }

}
