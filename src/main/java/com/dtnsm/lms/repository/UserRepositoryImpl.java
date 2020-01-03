package com.dtnsm.lms.repository;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Account>> findByParentUserId(String userId) {
        QAccount qAccount = QAccount.account;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.parentUserId.isNotNull().and(qAccount.parentUserId.eq(userId)));

        List<Account> results = queryFactory.selectFrom(qAccount)
                .where(builder)
                .orderBy(qAccount.engName.asc())
                .fetch();

        if(ObjectUtils.isEmpty(results)) {
            return Optional.empty();
        } else {
            return Optional.of(results);
        }
    }

    @Override
    public List<UserJobDescriptionDTO> getUserJobDescriptionByParentUserId(String parentUserId, JobDescriptionStatus status) {
        QAccount qAccount = QAccount.account;
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.parentUserId.eq(parentUserId));
        if(status == JobDescriptionStatus.SUPERSEDED) {
            builder.and(qUserJobDescription.status.eq(status));
        } else {
            builder.and(qUserJobDescription.status.ne(JobDescriptionStatus.SUPERSEDED));
        }

        return queryFactory.select(
                Projections.constructor(UserJobDescriptionDTO.class, qAccount, qUserJobDescription)
        )
        .from(qAccount).join(qUserJobDescription)
        .on(qAccount.userId.eq(qUserJobDescription.username))
        .where(builder)
        .orderBy(qAccount.engName.asc(), qUserJobDescription.id.desc())
        .fetch();
    }

    @Override
    public List<UserCurriculumVitaeDTO> getUserCurriculumVitaeList(String parentUserId, CurriculumVitaeStatus status) {
        QAccount qAccount = QAccount.account;
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.parentUserId.eq(parentUserId));
        if(status == CurriculumVitaeStatus.SUPERSEDED) {
            builder.and(qCurriculumVitae.status.eq(status));
        } else {
            builder.and(qCurriculumVitae.status.in(Arrays.asList(CurriculumVitaeStatus.CURRENT)));
        }

        return queryFactory.select(
                Projections.constructor(UserCurriculumVitaeDTO.class, qAccount, qCurriculumVitae)
        )
                .from(qAccount).join(qCurriculumVitae)
                .on(qAccount.userId.eq(qCurriculumVitae.account.userId))
                .where(builder)
                .orderBy(qAccount.engName.asc(), qCurriculumVitae.id.desc())
                .fetch();
    }
}
