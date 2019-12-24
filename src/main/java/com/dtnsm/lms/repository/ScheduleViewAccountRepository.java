package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BorderViewAccount;
import com.dtnsm.lms.domain.ScheduleViewAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScheduleViewAccountRepository extends JpaRepository<ScheduleViewAccount, Long> {

    List<ScheduleViewAccount> findAllBySchedule_IdAndAccount_UserId(long scheduleId, String userId);

    List<ScheduleViewAccount> findAllBySchedule_IdOrderByCreatedByDesc(long scheduleId);
}