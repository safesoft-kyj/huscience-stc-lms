package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        pageInfo.setParentTitle("교육과정");

        //courseMaster = courseMasterService.getById("A01");
    }

    @GetMapping("/list/{courseId}")
    public String list(@PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("<a href='/admin/course/list/" + course.getCourseMaster().getId() + "'>" + course.getCourseMaster().getCourseName() + "</a> > 강의");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", sectionService.getAllByCourseId(courseId));
        model.addAttribute("typeId", course.getCourseMaster().getId());
        model.addAttribute("gubunId", course.getCourseMaster().getCourseGubun().getMinorCd());

        return "admin/course/section/list";
    }

    @GetMapping("/add/{courseId}")
    public String noticeAdd(@PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        CourseSection courseSection = new CourseSection();
        courseSection.setCourse(course);

        pageInfo.setPageTitle(course.getTitle());

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
        courseSection.setMinute(Math.round(courseSection.getHour()*60));
        courseSection.setSecond(Math.round(courseSection.getMinute()*60));
        CourseSection courseSection1 = sectionService.saveSection(courseSection);

        // Section 저장
        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, courseSection1))
                .collect(Collectors.toList());


        // 강의 시간의 변경시 과정의 시간을 업데이트 한다.
        courseService.updateCourseHour(course);

        return "redirect:/admin/course/section/list/" + courseSection1.getCourse().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        pageInfo.setPageTitle(courseSection.getName());

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
        CourseSection oldCourseSection = sectionService.getCourseSectionById(id);

        courseSection.setCourseSectionActions(oldCourseSection.getCourseSectionActions());
        courseSection.setSectionFiles(oldCourseSection.getSectionFiles());
        courseSection.setCourse(oldCourseSection.getCourse());
        courseSection.setMinute(Math.round(courseSection.getHour()*60));
        courseSection.setSecond(Math.round(courseSection.getMinute()*60));

        CourseSection courseSection1 = sectionService.saveSection(courseSection);

        // 강의 시간의 변경시 과정의 시간을 업데이트 한다.
        courseService.updateCourseHour(courseSection1.getCourse());

        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, courseSection1))
                .collect(Collectors.toList());

        return "redirect:/admin/course/section/list/" + courseSection1.getCourse().getId();
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(courseSection.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSection.getSectionFiles());
        model.addAttribute("courseId", courseSection.getCourse().getId());

        return "admin/course/section/view";
    }


    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        CourseSection courseSection = sectionService.getCourseSectionById(id);

        Long courseId = courseSection.getCourse().getId();

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (courseSection.getCourse().getCourseAccountList().size() <= 0) {
            sectionService.deleteSection(courseSection);
        }

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
