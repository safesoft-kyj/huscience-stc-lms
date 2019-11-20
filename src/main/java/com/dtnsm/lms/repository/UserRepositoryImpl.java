package com.dtnsm.lms.repository;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.QAccount;
import com.dtnsm.lms.domain.UserJobDescriptionDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

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
    public List<UserJobDescriptionDTO> getUserJobDescriptionByParentUserId(String userId) {
        QAccount qAccount = QAccount.account;
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;

        return queryFactory.select(
                Projections.constructor(UserJobDescriptionDTO.class, qAccount, qUserJobDescription)
        )
        .from(qAccount).join(qUserJobDescription)
        .on(qAccount.userId.eq(qUserJobDescription.username))
        .where(qAccount.parentUserId.eq(userId))
        .orderBy(qAccount.engName.asc())
        .fetch();
    }
}
