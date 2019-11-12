package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BinderCv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BinderCvRepository extends JpaRepository<BinderCv, Long> {

    BinderCv findByAccount_UserIdAndIsActive(String userId, String isActive);

    List<BinderCv> findAllByAccount_UserIdOrderByVerDesc(String userId);

}
