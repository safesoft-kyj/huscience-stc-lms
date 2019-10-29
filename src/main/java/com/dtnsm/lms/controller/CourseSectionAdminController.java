package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.service.CourseSectionFileService;
import com.dtnsm.lms.service.CourseSectionService;
import com.dtnsm.lms.service.CourseMasterService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/course/section")
public class CourseSectionAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseSectionAdminController.class);

    @Autowired
    CodeService codeService;

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseService courseService;

    @Autowired
    private CourseSectionService sectionService;

    @Autowired
    private CourseSectionFileService fileService;

    @Autowired
    private UserMapperService userMapperService;

    private PageInfo pageInfo = new PageInfo();

    private CourseMaster courseMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0301";

    public CourseSectionAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");

        //courseMaster = courseMasterService.getById("A01");
    }

    @GetMapping("/add/{courseId}")
    public String noticeAdd(@PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        CourseSection courseSection = new CourseSection();
        courseSection.setCourse(course);
        courseSection.setType(codeService.getMinorById(minorCode));

        pageInfo.setPageTitle(course.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("courseSection", courseSection);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("minorCode", minorCode);
        model.addAttribute("id", courseId);

        return "admin/course/section/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseSection courseSection
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam("id") Long id
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        courseSection.setCourse(course);
        courseSection.setSecond(courseSection.getMinute()*60);

        CourseSection courseSection1 = sectionService.saveSection(courseSection);

        // Section 저장
        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, courseSection1))
                .collect(Collectors.toList());

        return "redirect:/admin/course/section/edit/" + courseSection1.getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        pageInfo.setPageTitle(courseSection.getName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("courseSection", courseSection);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseSection.getId());

        return "admin/course/section/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @RequestParam("files") MultipartFile[] files
            , @Valid CourseSection courseSection
            , BindingResult result) {
        if(result.hasErrors()) {
            courseSection.setId(id);
            return "/admin/course/section/list/" + courseSection.getId();
        }


//        List<CourseSectionActionHistory> courseSectionHistories = sectionService.getCourseSectionById(id).getCourseSectionHistories();
//        courseSection.setCourseSectionHistories(courseSectionHistories);
//        List<CourseSectionFile> courseSectionFile= sectionService.getCourseSectionById(id).getSectionFiles();
//        courseSection.setSectionFiles(courseSectionFile);
//        courseSection.setSecond(courseSection.getMinute()*60);
//
//        CourseSection courseSurveyOld = sectionService.getCourseSectionById(id);
//
//        Course course = courseSurveyOld.getCourse();
//        courseSection.setCourse(course);
//
//        CourseSection courseSurvey1 = sectionService.saveSection(courseSection);

//        Arrays.asList(files)
//                .stream()
//                .map(file -> fileService.storeFile(file, courseSurvey1))
//                .collect(Collectors.toList());

        return "redirect:/admin/course/section/edit/" + courseSection.getId();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        Long courseId = courseSection.getCourse().getId();

        sectionService.deleteSection(courseSection);

        return "redirect:/admin/course/section/edit/" + courseId;
    }


    @GetMapping("/delete-file/{section_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long section_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/course/section/edit/" + section_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseSectionFile courseSectionFile =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseSectionFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseSectionFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseSectionFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}
