package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuizAction;
import com.dtnsm.lms.domain.CourseQuizActionAnswer;
import com.dtnsm.lms.domain.CourseSectionAction;
import com.dtnsm.lms.repository.CourseQuizQuestionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionRepository;
import com.dtnsm.lms.repository.CourseSectionActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSectionActionService {

    @Autowired
    CourseSectionActionRepository sectionActionRepository;

    public CourseSectionAction save(CourseSectionAction sectionAction) {
        return sectionActionRepository.save(sectionAction);
    }

    public void delete(CourseSectionAction sectionAction) {

        sectionActionRepository.delete(sectionAction);
    }

    public void delete(Long id) {
        sectionActionRepository.delete(getById(id));
    }

    public CourseSectionAction getById(Long id) {
        return sectionActionRepository.findById(id).get();
    }

    public CourseSectionAction getBySectionIdAndUserId(long sectionId, String userId) {
        return sectionActionRepository.findByCourseSection_IdAndAccount_UserId(sectionId, userId);
    }

    public List<CourseSectionAction> getAllByUserId(String userId) {
        return sectionActionRepository.findAllByAccount_UserId(userId);
    }

}