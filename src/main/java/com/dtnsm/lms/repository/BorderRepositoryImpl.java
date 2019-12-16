package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.QBorder;
import com.dtnsm.lms.domain.QBorderViewAccount;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;


public class BorderRepositoryImpl extends QuerydslRepositorySupport implements CustomBorderRepository {

    public BorderRepositoryImpl() {
        super(Border.class);
    }


    @Override
    public List<Border> findAllBorderByViewUser(String userId) {
        return from(QBorder.border)
                .leftJoin(QBorder.border.borderViewAccounts, QBorderViewAccount.borderViewAccount)
                .fetchJoin()
                .where(QBorderViewAccount.borderViewAccount.account.userId.eq(userId))
                .fetch();
    }
}