package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseManager;
import com.dtnsm.lms.repository.CourseManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseManagerService {

    CourseManagerRepository courseManagerRepository;

    @Autowired
    UserServiceImpl userService;

    public CourseManagerService(CourseManagerRepository courseManagerRepository) {
        this.courseManagerRepository = courseManagerRepository;
    }

    public List<CourseManager> getList() {
        return courseManagerRepository.findAll();
    }

    public CourseManager getByUserId(String userId) {

        return courseManagerRepository.findById(userId).get();
    }

    public CourseManager save(CourseManager courseManager){

        return courseManagerRepository.save(courseManager);
    }

    public void delete(CourseManager courseManager) {
            courseManagerRepository.delete(courseManager);
    }

    public CourseManager getCourseManager() {
        List<CourseManager> managers = courseManagerRepository.findAll();
        if (managers.size() > 0) return managers.get(0);
        else return null;
    }
}
