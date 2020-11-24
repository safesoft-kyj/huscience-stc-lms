package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <Account, String >, QuerydslPredicateExecutor<Account>, UserRepositoryCustom {
    Account findByName(String name);
    Account findByEmail(String email);
    Account findByUserId(String userId);
    Optional<Account> findByUserIdAndPassword(String userId, String password);
    Optional<List<Account>> findAllByParentUserIdAndEnabled(String parentUserId, Boolean enabled);

}