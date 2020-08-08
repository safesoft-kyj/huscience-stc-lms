package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.domain.mapper.BorderMasterMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorderMasterRepository extends JpaRepository<BorderMaster, String> {
    List<BorderMasterMapper> findAllBy();
}