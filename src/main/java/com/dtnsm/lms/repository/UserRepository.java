package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <Account, String >, QuerydslPredicateExecutor<Account>, UserRepositoryCustom {
    Account findByName(String name);
    Account findByEmail(String email);
    Account findByUserId(String userId);
    Account findByUserIdAndPassword(String userId, String password);
}