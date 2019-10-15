package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;


    public Schedule save(Schedule schedule){
        return scheduleRepository.save(schedule);
    }

    public void delete(Schedule schedule) {

        scheduleRepository.delete(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.delete(getScheduleById(id));
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).get();
    }

    public Schedule getScheduleTop1ByYear(String type) {
        return scheduleRepository.findTop1ByYear(type);
    }

    public List<Schedule> getListByYear(String type) {
        return scheduleRepository.findAllByYear(type);
    }

    public List<Schedule> getList() {
        return scheduleRepository.findAll();
    }




}
