package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuizActionHistory;
import com.dtnsm.lms.domain.CourseSectionActionHistory;
import com.dtnsm.lms.repository.CourseQuizActionHistoryRepository;
import com.dtnsm.lms.repository.CourseSectionActionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseQuizActionHistoryService {

    @Autowired
    CourseQuizActionHistoryRepository courseQuizActionHistoryRepository;

    public CourseQuizActionHistory save(CourseQuizActionHistory quizActionHistory) {
        return courseQuizActionHistoryRepository.save(quizActionHistory);
    }

    public void delete(CourseQuizActionHistory quizActionHistory) {

        courseQuizActionHistoryRepository.delete(quizActionHistory);
    }

    public void delete(Long id) {
        courseQuizActionHistoryRepository.delete(getCourseQuizActionById(id));
    }

    public CourseQuizActionHistory getCourseQuizActionById(Long id) {
        return courseQuizActionHistoryRepository.findById(id).get();
    }
}