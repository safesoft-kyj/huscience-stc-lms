package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.repository.CourseAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAccountService {

    @Autowired
    CourseAccountRepository courseAccountRepository;

    public CourseAccount save(CourseAccount courseAccount){
        return courseAccountRepository.save(courseAccount);
    }

    public void delete(CourseAccount courseAccount) {

        courseAccountRepository.delete(courseAccount);
    }

    public void delete(String userId) {
        courseAccountRepository.deleteCourseAccountByAccount_UserId(userId);
    }

    public void delete(long courseId) {
        courseAccountRepository.deleteCourseAccountByCourse_Id(courseId);
    }

    public List<CourseAccount> getCourseAccountByUserId(String userId) {
        return courseAccountRepository.findByAccount_UserId(userId);
    }

    public List<CourseAccount> getCourseAccountByCourseId(long courseId) {
        return courseAccountRepository.findByCourse_Id(courseId);
    }
}
