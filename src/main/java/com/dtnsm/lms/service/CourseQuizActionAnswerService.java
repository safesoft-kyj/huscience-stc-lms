package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuizActionAnswer;
import com.dtnsm.lms.domain.CourseQuizActionHistory;
import com.dtnsm.lms.repository.CourseQuizActionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizActionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseQuizActionAnswerService {

    @Autowired
    CourseQuizActionAnswerRepository courseQuizActionAnswerRepository;

}

