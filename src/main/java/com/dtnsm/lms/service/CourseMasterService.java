package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.repository.CourseMasterRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMasterService {
    @Autowired
    CourseMasterRepository courseMasterRepository;

    @Autowired
    CourseService courseService;

    @Autowired
    CodeService codeService;

    private static String majorCd = "BC01";

    public CourseMasterService(CourseMasterRepository courseMasterRepository) {
        this.courseMasterRepository = courseMasterRepository;
    }

    public List<CourseMaster> getList() {
        return courseMasterRepository.findAll();
    }

    public Iterable<CourseMaster> getListBySelfAndClass() {
        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseMaster.id.in("BC0101", "BC0102"));
        return courseMasterRepository.findAll(builder);
    }


    public Page<CourseMaster> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        return courseMasterRepository.findAll(pageable);
    }

    public CourseMaster getById(String id) {

        return courseMasterRepository.findById(id).get();
    }

    public CourseMaster save(CourseMaster courseGroup){

        return courseMasterRepository.save(courseGroup);
    }

    public void delete(CourseMaster courseGroup) {

        courseMasterRepository.delete(courseGroup);
    }

    public List<ElMinor> getTypeList() {
        return codeService.getMinorList(majorCd);
    }

    public String getCourseMasterNumber() {
        return String.format("%s%02d", majorCd, courseMasterRepository.count() + 1);
    }
}
