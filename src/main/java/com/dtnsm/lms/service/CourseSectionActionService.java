package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseSectionAction;
import com.dtnsm.lms.domain.constant.SectionStatusType;
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

    // self 교육이 아닌경우 강의상태를 종료 시킨다.
    public CourseSectionAction UpdateCourseSectionActionComplete(CourseSectionAction courseSectionAction) {

        CourseAccount courseAccount = courseSectionAction.getCourseAccount();

        CourseSectionAction courseSectionAction1 = null;

        if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {

            // 1.강의 처리(self 교육이외에는 실제 강의가 없음)
            // 강의를 종료 시킨다.
            courseSectionAction.setStatus(SectionStatusType.COMPLETE);
            courseSectionAction1 = sectionActionRepository.save(courseSectionAction);
        }

        return courseSectionAction1;
    }

}