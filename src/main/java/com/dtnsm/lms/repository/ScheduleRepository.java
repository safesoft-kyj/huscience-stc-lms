package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findTop1ByYear(String year);

    List<Schedule> findAllByYear(String year);

}