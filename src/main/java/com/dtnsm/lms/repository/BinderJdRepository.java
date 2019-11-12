package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BinderJd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BinderJdRepository extends JpaRepository<BinderJd, Long> {
    BinderJd findByAccount_UserIdAndIsActive(String userId, String isActive);

    BinderJd findByAccount_UserIdAndJdVersion_IdAndIsActive(String userId, long versionId, String isActive);

    List<BinderJd> findAllByAccount_UserIdAndIsActive(String userId, String isActive);

    List<BinderJd> findAllByAccount_UserId(String userId);

    List<BinderJd> findAllByJdVersion_Id(long versionId);

    List<BinderJd> findAllByIsActive(String isActive);
}
