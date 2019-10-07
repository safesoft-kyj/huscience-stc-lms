package com.dtnsm.lms.course.controller;

import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseFile;
import com.dtnsm.lms.course.domain.CourseSection;
import com.dtnsm.lms.course.service.*;
import com.dtnsm.lms.file.FileService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/course")
public class CourseController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseService courseService;

    @Autowired
    private CourseSectionService sectionService;

    @Autowired
    private CourseSurveyService surveyService;

    @Autowired
    private CourseQuizService quizService;

    @Autowired
    private CourseFileService fileService;

    private PageInfo pageInfo = new PageInfo();

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");
    }

    @GetMapping("/list/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId, @PageableDefault Pageable pageable, Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //courseService.deleteBlankCourse();

        String courseName = courseMasterService.getById(typeId).getCourseName();
        pageInfo.setPageTitle(courseName + "조회");

        Page<Course> courses = courseService.getPageList(typeId, pageable);
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);

        return "content/course/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);

        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "content/course/view";
    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseFile courseFile =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + courseFile.getFileName() + "\"")
                .body(resource);
    }


}