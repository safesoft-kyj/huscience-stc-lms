package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.MunjeBank;
import com.dtnsm.lms.domain.constant.MunjeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MunjeBankRepository extends JpaRepository<MunjeBank, Long> {

    List<MunjeBank> findAllByMunjeType(MunjeType munjeType);
}