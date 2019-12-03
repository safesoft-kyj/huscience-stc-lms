package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

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


     /*
        Employee Training Matrix 연 일정
     */
}
