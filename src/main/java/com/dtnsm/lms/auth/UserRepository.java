package com.dtnsm.lms.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository < Account, Long > {
    Account findByEmail(String email);
    Account findByUserId(String userId);
}