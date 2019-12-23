package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.repository.ScheduleRepository;
import com.dtnsm.lms.repository.ScheduleViewAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleViewAccountRepository scheduleViewAccountRepository;

    @Autowired
    UserServiceImpl userService;

    /*
        Calenda 연 일정
     */

    public Schedule save(Schedule schedule){

        return scheduleRepository.save(schedule);
    }

    public void delete(Schedule schedule) {

        scheduleRepository.delete(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.delete(getById(id));
    }

    public Schedule getById(Long id) {
        return scheduleRepository.findById(id).get();
    }

    public Schedule getTop1BySctypeOrderByCreatedDateDesc(ScheduleType sctype) {
        return scheduleRepository.findTop1BySctypeOrderByCreatedDateDesc(sctype);
    }

    public Schedule getTop1BySctypeAndTitleLikeOrderByCreatedDateDesc(ScheduleType sctype, String title) {
        return scheduleRepository.findTop1BySctypeAndTitleLikeOrderByCreatedDateDesc(sctype, title + "%");
    }

    public Schedule getTop1BySctypeAndTitleLike(ScheduleType sctype, String title) {
        return scheduleRepository.findTop1BySctypeAndTitleLike(sctype,  title + "%");
    }

    public List<Schedule> getList() {
        return scheduleRepository.findAll();
    }


    public List<Schedule> getListBySctypeOrderByCreatedDateDesc(ScheduleType sctype) {
        return scheduleRepository.findAllBySctypeOrderByCreatedDateDesc(sctype);
    }


    public Schedule getByIsActive(ScheduleType sctype, int isActive) {
        return scheduleRepository.findBySctypeAndIsActive(sctype, isActive);
    }

    // 조회수 증가
    public void updateViewCnt(long scheduleId, String userId) {

        List<ScheduleViewAccount> scheduleViewAccounts = scheduleViewAccountRepository.findAllBySchedule_IdAndAccount_UserId(scheduleId, userId);


        // 최초 읽음수만 증가 시킨다.
        if(scheduleViewAccounts.size() > 0) return;


        Schedule schedule = getById(scheduleId);
        Account account = userService.getAccountByUserId(userId);

        if (account == null || schedule == null) return;
        scheduleViewAccountRepository.save(new ScheduleViewAccount(schedule, account));

        schedule.setViewCnt(schedule.getViewCnt() + 1);
        scheduleRepository.save(schedule);
    }

    public List<ScheduleViewAccount> getAllScheduleAccountByScheduleId(long scheduleId) {
        List<ScheduleViewAccount> scheduleViewAccounts = scheduleViewAccountRepository.findAllBySchedule_IdOrderByCreatedByDesc(scheduleId);
        return scheduleViewAccounts;
    }

     /*
        Employee Training Matrix 연 일정
     */
}
