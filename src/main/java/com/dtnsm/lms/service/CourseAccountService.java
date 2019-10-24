package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.repository.CourseAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public CourseAccount getByCourseIdAndUserId(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserId(courseId, userId);
    }


    public CourseAccount getByCourseIdAndApprUserId1(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndApprUserId1_UserId(courseId, userId);
    }

    public CourseAccount getByCourseIdAndApprUserId2(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndApprUserId2_UserId(courseId, userId);
    }

    public List<CourseAccount> getCourseAccountByCourseId(long courseId) {
        return courseAccountRepository.findByCourse_Id(courseId);
    }

    public List<CourseAccount> getCourseAccountByUserId(String userId) {
        return courseAccountRepository.findByAccount_UserId(userId);
    }

    // 내신청함
    public Page<CourseAccount> getListUserId(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장)
    public Page<CourseAccount> getListApprUserId1(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId1_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId1AndIsAppr1(Pageable pageable, String userId, String isAppr1) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId1_UserIdAndIsAppr1(userId, isAppr1, pageable);
    }

    // 2차결재자(과정 관리자) 전체
    public Page<CourseAccount> getListApprUserId2(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId2_UserId(userId, pageable);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId2AndIsAppr2(Pageable pageable, String userId, String isAppr2) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId2_UserIdAndIsAppr2(userId, isAppr2, pageable);
    }

}
