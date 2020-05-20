package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseMaster;
import com.dtnsm.lms.domain.CourseSection;
import com.dtnsm.lms.domain.CourseSectionFile;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/course")
public class CourseSectionAdminController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseSectionAdminController.class);

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
        pageInfo.setParentTitle("교육과정");

        //courseMaster = courseMasterService.getById("A01");
    }

    @GetMapping("/{typeId}/{courseId}/section")
    public String list(@PathVariable("typeId") String typeId, @PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "강의"));
        model.addAttribute(pageInfo);
        model.addAttribute("borders", sectionService.getAllByCourseId(courseId));

        model.addAttribute("gubunId", course.getCourseMaster().getCourseGubun().getMinorCd());
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/section/list";
    }

    @GetMapping("/{typeId}/{courseId}/section/add")
    public String noticeAdd(@PathVariable("typeId") String typeId, @PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        CourseSection courseSection = new CourseSection();
        courseSection.setCourse(course);

        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "강의"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseSection", courseSection);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("minorCode", minorCode);
        model.addAttribute("id", courseId);
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/section/add";
    }

    @PostMapping("/{typeId}/{courseId}/section/add-post")
    public String noticeAddPost(@Valid CourseSection courseSection
            , @PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam(value = "files", required = false) MultipartFile file
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add";
        }

        Course course = courseService.getCourseById(courseId);
        courseSection.setCourse(course);
        courseSection.setMinute(Math.round(courseSection.getHour()*60));
        courseSection.setSecond(Math.round(courseSection.getMinute()*60));
        CourseSection courseSection1 = sectionService.saveSection(courseSection);

        if (file != null && !file.isEmpty()) {
            CourseSectionFile courseSectionFile = fileService.storeFile(file, courseSection1);
            courseSection1.setImageSize(courseSectionFile.getImageSize());
            courseSection1 = sectionService.saveSection(courseSection1);
        }

        // Section 저장
//        Arrays.asList(files)
//                .stream()
//                .map(file -> fileService.storeFile(file, courseSection1))
//                .collect(Collectors.toList());



        // 강의 시간의 변경시 과정의 시간을 업데이트 한다.
        courseService.updateCourseHour(course);

        return String.format("redirect:/admin/course/%s/%s/section?page=%s"
                , courseSection1.getCourse().getCourseMaster().getId()
                , courseSection1.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/section/edit")
    public String noticeEdit(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("id") long id, Model model) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);
        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "강의"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseSection", courseSection);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("id", courseSection.getId());
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/section/edit";
    }

    @PostMapping("/{typeId}/{courseId}/section/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @PathVariable("typeId") String typeId
            , @RequestParam(value = "files", required = false ) MultipartFile file
            , @Valid CourseSection courseSection
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            courseSection.setId(id);
            return "admin/course/section/edit";
        }
        CourseSection oldCourseSection = sectionService.getCourseSectionById(id);

        courseSection.setCourseSectionActions(oldCourseSection.getCourseSectionActions());
        courseSection.setSectionFiles(oldCourseSection.getSectionFiles());
        courseSection.setCourse(oldCourseSection.getCourse());
        courseSection.setMinute(Math.round(courseSection.getHour()*60));
        courseSection.setSecond(Math.round(courseSection.getMinute()*60));

        CourseSection courseSection1 = sectionService.saveSection(courseSection);

        // 강의 시간의 변경시 과정의 시간을 업데이트 한다.
        courseService.updateCourseHour(courseSection1.getCourse());

        if (file != null && !file.isEmpty()) {
            CourseSectionFile courseSectionFile = fileService.storeFile(file, courseSection1);
            courseSection1.setImageSize(courseSectionFile.getImageSize());
            courseSection1 = sectionService.saveSection(courseSection1);
        }

//        Arrays.asList(files)
//                .stream()
//                .map(file -> fileService.storeFile(file, courseSection1))
//                .collect(Collectors.toList());

        return String.format("redirect:/admin/course/%s/%s/section?page=%s"
                , courseSection1.getCourse().getCourseMaster().getId()
                , courseSection1.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/section/view/{id}")
    public String viewPage(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId, @PathVariable("id") long id, Model model) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "강의"));

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSection.getSectionFiles());
        model.addAttribute("courseId", courseSection.getCourse().getId());
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", courseSection.getCourse().getTitle());

        return "admin/course/section/view";
    }


    @GetMapping("/{typeId}/{courseId}/section/delete/{id}")
    public String noticeDelete(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable("id") long id
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , HttpServletRequest request) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (courseSection.getCourse().getCourseAccountList().size() <= 0) {
            sectionService.deleteSection(courseSection);
        }

        // 이전 URL를 리턴한다.
//        String refUrl = request.getHeader("referer");
//        return "redirect:" +  refUrl;

        return String.format("redirect:/admin/course/%s/%s/section?page=%s"
                , typeId
                , courseId
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/section/delete-file/{section_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , @PathVariable long section_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        CourseSection courseSection = sectionService.getCourseSectionById(section_id);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }

    @GetMapping("/{typeId}/{courseId}/section/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable long id, HttpServletRequest request){

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

    @GetMapping("/section/image/download-file/{fileId}/{pageId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadImageFile(@PathVariable("fileId") long fileId
            , @PathVariable("pageId") Integer pageId
            , HttpServletRequest request){

        CourseSectionFile courseSectionFile =  fileService.getUploadFile(fileId);

        //
        String fileName = pageId + ".jpg";

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(Paths.get(courseSectionFile.getImageFolder(), fileName).toString());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/jpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
