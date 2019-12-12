package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BorderViewAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BorderViewAccountRepository extends JpaRepository<BorderViewAccount, Long> {

    List<BorderViewAccount> findAllByBorder_IdAndAccount_UserId(long borderId, String userId);

    List<BorderViewAccount> findAllByBorder_IdOrderByCreatedByDesc(long borderId);
}