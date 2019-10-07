package com.dtnsm.lms.course.service;

import com.dtnsm.lms.course.domain.CourseSection;
import com.dtnsm.lms.course.domain.CourseSectionFile;
import com.dtnsm.lms.course.repository.CourseSectionRepository;
import com.dtnsm.lms.file.FileUploadException;
import com.dtnsm.lms.file.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CourseSectionService {

    @Autowired
    CourseSectionRepository sectionRepository;

    @Autowired
    CourseSectionFileService sectionFileService;

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

}
