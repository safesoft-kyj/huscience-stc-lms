package com.dtnsm.lms.service;

import com.dtnsm.common.entity.JobDescription;
import com.dtnsm.common.entity.QJobDescription;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseFile;
import com.dtnsm.lms.domain.CourseSection;
import com.dtnsm.lms.domain.QCourse;
import com.dtnsm.lms.repository.CourseRepository;
import com.dtnsm.lms.util.DateUtil;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseFileService courseFileService;

    @Autowired
    CourseService courseService;

    @Autowired
    CodeService codeService;

    // 과정 상태 변경은 크론에서 한다. /component/CourseScheduler
    private Course updateStatus(Course course) {
        Date requestFromDate = DateUtil.getStringToDate(course.getRequestFromDate());
        Date requestToDate = DateUtil.getStringToDate(course.getRequestToDate());
        Date fromDate = DateUtil.getStringToDate(course.getFromDate());
        Date toDate = DateUtil.getStringToDate(course.getToDate());
        Date toDay = DateUtil.getToday();


        int todayReqFromCompare = toDay.compareTo(requestFromDate);  // 1 : 현재일이 크다, -1 : 요청시작일이 크다
        int todayReqToCompare = toDay.compareTo(requestToDate);      // 1 : 현재일이 크다ㅣ
        int todayFromCompare = toDay.compareTo(fromDate);
        int todayToCompare = toDay.compareTo(toDate);

        int status = 0;

        if(todayReqFromCompare < 0)
            status = 1; // 신청기간이전 : 신청대기
        else if (todayReqFromCompare >= 0 && todayReqToCompare <= 0) {
            status = 2; // 요청기간중 : 교육신청
        } else if(todayReqToCompare > 0 && todayFromCompare<0) {
            status = 3; // 신청이후 교육 대기 : 교육대기
        } else if (todayFromCompare >= 0 && todayToCompare <= 0) {
            status = 4; // 교육기간중 : 교육중
        } else if(todayToCompare > 0){
            status = 5; // 교육종료
        }

        // 요청시작일자 이전이면 신청대기
        // 요청일자 사이이면 교육신청
        // 요청종료일 이후 이면서 교육시작일자 이전이면 교육대기
        // 교육일자 사이이면 교육중
        // 교육일자 이후이면 교육종료

        if (course.getStatus() != status) {

            course.setStatus(status);
            course = courseService.save(course);
        }

        return course;
    }

    public CourseService(CourseRepository elcCourseRepository) {
        this.courseRepository = elcCourseRepository;
    }

    public List<Course> getList() {
        return courseRepository.findAll();
    }

    public Page<Course> getPageList(int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        Page<Course> courses = courseRepository.findAllByActiveGreaterThan(active, pageable);
//
//        for(Course course: courses ) {
//            updateStatus(course);
//        }

        return courses;
    }

    public Page<Course> getPageLisByTypeId(String typeId, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        Page<Course> courses = courseRepository.findAllByCourseMaster_IdAndActiveGreaterThan(typeId, active, pageable);

//        for(Course course: courses ) {
//            updateStatus(course);
//        }

        return courses;
    }

    public Page<Course> getPageLisByTypeId(String typeId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        Page<Course> courses = courseRepository.findAllByCourseMaster_Id(typeId, pageable);

//        for(Course course: courses ) {
//            updateStatus(course);
//        }

        return courses;
    }

    public Page<Course> getPageLisByTypeIdAndTitleLike(String typeId, String title, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        Page<Course> courses = courseRepository.findAllByCourseMaster_IdAndTitleLikeAndActiveGreaterThan(typeId, '%' + title + '%', active, pageable);

//        for(Course course: courses ) {
//            updateStatus(course);
//        }

        return courses;
    }

    public Page<Course> getPageListByTitleLike(String title, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        Page<Course> courses = courseRepository.findAllByTitleLikeAndActiveGreaterThan('%' + title + '%', active, pageable);

//        for(Course course: courses ) {
//            updateStatus(course);
//        }

        return courses;
    }

    public Page<Course> getPageList(String typeId, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByCourseMaster_IdAndActiveGreaterThan(typeId, active, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Course> getPageListByTitleLikeOrContentLike(String typeId, String title, String content, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByCourseMaster_IdAndTitleLikeOrContentLikeAndActiveGreaterThan(typeId,"%" + title + "%", "%" + content + "%", active, pageable);
    }

    // 제목 Like 검색
    public Page<Course> getPageListByTitleLike(String typeId, String title, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByCourseMaster_IdAndTitleLikeAndActiveGreaterThan(typeId, "%" + title + "%", active, pageable);
    }

    // 내용 Like 검색
    public Page<Course> getPageListByContentLike(String typeId, String content, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByCourseMaster_IdAndContentLikeAndActiveGreaterThan(typeId, "%" + content + "%", active, pageable);
    }



    public Course getCourseById(Long id) {

        return courseRepository.findById(id).get();
    }

    public Course save(Course elCourse){
        Course course = courseRepository.save(elCourse);
        // 코스 저장후 상태를 업데이트 한다.
        updateStatus(course);
        return course;
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
        return courseRepository.findAllByTitleAndActiveGreaterThan("", 0);
    }

    public void deleteBlankCourse() {

        for(Course course : getBlankCourse()) {
            this.delete(course);
        }
    }

    public long getCount() {
        return courseRepository.count();
    }


    // 회사 전체 월간 교육 일정
    public List<Course> getCourseByFromDateBetween(String fromDate, String toDate, int status) {
        return courseRepository.findByFromDateBetweenAndActiveGreaterThan(fromDate, toDate, status);
    }

    // 회사 전체 월간 교육 신청 일정
    public List<Course> getCourseByRequestFromDateBetween(String fromDate, String toDate, int status) {
        return courseRepository.findByRequestFromDateBetweenAndActiveGreaterThan(fromDate, toDate, status);
    }

    // 홈화면 일정 Top5
    public List<Course> getCourseTop5ByRequestFromDateBetween(String fromDate, String toDate, int status) {
        return courseRepository.findTop5ByRequestFromDateBetweenAndActiveGreaterThan(fromDate, toDate, status);
    }

    // 신입사원 필수교육(페이징)
    public Page<Course> getAllByIsNewEmpCourse(String isNewEmpCourse, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseRepository.findAllByIsNewEmpCourse(isNewEmpCourse, pageable);
    }

    // 신입사원 필수교육
    public List<Course> getAllByIsNewEmpCourse(String isNewEmpCourse) {
        return courseRepository.findAllByIsNewEmpCourse(isNewEmpCourse);
    }

    private Page<Course> findAllByUserRequest(String gubunId, String typeId, String title, int active, Pageable pageable) {
        QCourse qCourse = QCourse.course;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourse.courseMaster.courseGubun.minorCd.like(gubunId));
        builder.and(qCourse.courseMaster.id.like(typeId));
        builder.and(qCourse.active.eq(active));
        builder.and(qCourse.title.like(title));
        return courseRepository.findAll(builder, pageable);
    }

    public Page<Course> getAllByUserRequest(String gubunId, String typeId, String title, int active, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return findAllByUserRequest(gubunId + "%", typeId + "%", "%" + title + "%", active, pageable);
    }



    // 과정에 속한 강의 시간을 더하여 과정의 시간을 업데이트 한다.
    public void updateCourseHour(Course course) {

        if (course.getSections().size() > 0) {
            //과정의 강의시간을 합하여 과정의 시간으로 업데이트 한다.
            float sumHour = 0;
            for (CourseSection s : course.getSections()) {
                sumHour += s.getHour();
            }
            course.setHour(sumHour);
            courseService.save(course);
        }
    }
}
