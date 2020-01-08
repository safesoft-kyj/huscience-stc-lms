package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;

import com.dtnsm.lms.repository.CourseTrainingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CourseTraingLogService {

    @Autowired
    CourseTrainingLogRepository courseTrainingLogRepository;



    public Page<CourseTrainingLog> getAllByAccount_UserId(String userId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdOrderByCompleteDateDesc(userId, pageable);

        return courseTrainingLogs;
    }

}
