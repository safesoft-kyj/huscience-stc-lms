package com.dtnsm.lms.course.service;

import com.dtnsm.lms.base.ElCodeService;
import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseFile;
import com.dtnsm.lms.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseFileService courseFileService;

    @Autowired
    ElCodeService codeService;

    public CourseService(CourseRepository elcCourseRepository) {
        this.courseRepository = elcCourseRepository;
    }

    public List<Course> getList() {
        return courseRepository.findAll();
    }

    public Page<Course> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAll(pageable);
    }

    public Page<Course> getPageList(String typeId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByCourseMaster_Id(typeId, pageable);
    }

    public Course getCourseById(Long id) {

        return courseRepository.findById(id).get();
    }

    public Course save(Course elCourse){

        return courseRepository.save(elCourse);
    }

    public void delete(Course course) {
        // 게시판 데이터및 파일 데이터 delete
        courseRepository.delete(course);

        // 첨부파일 삭제
        for(CourseFile courseFile : course.getCourseFiles()) {
            courseFileService.deleteFile(courseFile);
        }
    }

    // 게시판 조회수 증가
    public void updateViewCnt(Long id) {
        Course course = getCourseById(id);
        course.setViewCnt(course.getViewCnt() + 1);
        courseRepository.save(course);
    }

    private List<Course> getBlankCourse() {
        return courseRepository.findAllByTitle("");
    }

    public void deleteBlankCourse() {

        for(Course course : getBlankCourse()) {
            this.delete(course);
        }
    }

    public long getCount() {
        return courseRepository.count();
    }
}
