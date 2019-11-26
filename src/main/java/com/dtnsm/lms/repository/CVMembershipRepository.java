package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVMembershipRepository extends JpaRepository<CVMembership, Integer>, QuerydslPredicateExecutor<CVMembership> {
}
