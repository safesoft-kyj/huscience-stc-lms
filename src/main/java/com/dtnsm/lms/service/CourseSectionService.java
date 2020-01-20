package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.SectionStatusType;
import com.dtnsm.lms.repository.CourseSectionRepository;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CourseSectionService {

    @Autowired
    CourseSectionRepository sectionRepository;

    @Autowired
    CourseSectionFileService sectionFileService;

    @Autowired
    CourseSectionActionService courseSectionActionService;




    @Autowired
    CodeService codeService;

    private final Path fileLocation;

    @Autowired
    public CourseSectionService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getCourseSectionUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        }catch(Exception e) {
            throw new FileUploadException("파일을 업로드할 디렉토리를 생성하지 못했습니다.", e);
        }
    }

     /*
        Section Service
     */

    public List<CourseSection> getAllByCourseId(long courseId) {
        return sectionRepository.findAllByCourse_Id(courseId);
    }

    public CourseSection saveSection(CourseSection courseSection){

        return sectionRepository.save(courseSection);
    }

    public CourseSection saveQuiz(CourseSection section){
        return sectionRepository.save(section);
    }

    public void deleteSection(CourseSection section) {

        sectionRepository.delete(section);

        // 첨부파일 삭제
        for(CourseSectionFile courseSectionFile : section.getSectionFiles()) {
            sectionFileService.deleteFile(courseSectionFile);
        }
    }

    public void deleteSection(Long id) {
        sectionRepository.delete(getCourseSectionById(id));
    }

    public CourseSection getCourseSectionById(Long id) {
        return sectionRepository.findById(id).get();
    }


    // 교육과정의 첫번재 강의를 등록한다.
    public void CreateAutoSection(Course course) {

        // 과정에 등록된 강의 시간 정보를 가지고 온다.
        float hour = course.getHour();

        CourseSection courseSection = new CourseSection();
        courseSection.setName(course.getTitle());
        courseSection.setDescription("");
        courseSection.setTeacher("");
        courseSection.setStudyDate(course.getToDate());
        courseSection.setCourse(course);
        courseSection.setHour(hour);
        courseSection.setMinute(Math.round(courseSection.getHour() * 60));
        courseSection.setSecond(Math.round(courseSection.getMinute() * 60));
        saveSection(courseSection);
    }

    // 교육과정의 첫번재 강의를 등록한다.
    public void CreateAutoSection(Course course, MultipartFile file) {

        // 과정에 등록된 강의 시간 정보를 가지고 온다.
        float hour = course.getHour();

        CourseSection courseSection = new CourseSection();
        courseSection.setName(course.getTitle());
        courseSection.setDescription("");
        courseSection.setTeacher("");
        courseSection.setStudyDate(course.getFromDate());
        courseSection.setCourse(course);
        courseSection.setHour(hour);
        courseSection.setMinute(Math.round(courseSection.getHour() * 60));
        courseSection.setSecond(Math.round(courseSection.getMinute() * 60));
        CourseSection courseSection1 = saveSection(courseSection);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {
            CourseSectionFile courseSectionFile = sectionFileService.storeFile(file, courseSection1);
            courseSection1.setImageSize(courseSectionFile.getImageSize());
            courseSection1 = sectionRepository.save(courseSection1);
        }
    }
}
