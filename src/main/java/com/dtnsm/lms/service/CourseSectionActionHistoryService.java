package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseSectionAction;
import com.dtnsm.lms.domain.CourseSectionActionHistory;
import com.dtnsm.lms.repository.CourseSectionActionHistoryRepository;
import com.dtnsm.lms.repository.CourseSectionActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseSectionActionHistoryService {

    @Autowired
    CourseSectionActionHistoryRepository courseSectionActionHistoryRepository;

    public CourseSectionActionHistory save(CourseSectionActionHistory sectionActionHistory) {
        return courseSectionActionHistoryRepository.save(sectionActionHistory);
    }

    public void delete(CourseSectionActionHistory sectionActionHistory) {

        courseSectionActionHistoryRepository.delete(sectionActionHistory);
    }

    public void delete(Long id) {
        courseSectionActionHistoryRepository.delete(getCourseQuizActionById(id));
    }

    public CourseSectionActionHistory getCourseQuizActionById(Long id) {
        return courseSectionActionHistoryRepository.findById(id).get();
    }
}