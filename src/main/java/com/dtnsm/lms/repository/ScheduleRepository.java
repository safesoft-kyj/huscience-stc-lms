package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.service.ScheduleService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findTop1BySctypeOrderByCreatedDateDesc(ScheduleType sctype);
    Schedule findTop1BySctypeAndTitleLikeOrderByCreatedDateDesc(ScheduleType sctype, String title);

    Schedule findTop1BySctypeAndTitleLike(ScheduleType sctype, String Title);

    List<Schedule> findAllBySctypeOrderByCreatedDateDesc(ScheduleType sctype);

    Schedule findBySctypeAndIsActive(ScheduleType sctype, int isActive);
}