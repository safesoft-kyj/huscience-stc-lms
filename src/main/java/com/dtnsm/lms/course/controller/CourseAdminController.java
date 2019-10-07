package com.dtnsm.lms.course.controller;

import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseFile;
import com.dtnsm.lms.course.domain.CourseMaster;
import com.dtnsm.lms.course.service.CourseFileService;
import com.dtnsm.lms.course.service.CourseSectionService;
import com.dtnsm.lms.course.service.CourseMasterService;
import com.dtnsm.lms.course.service.CourseService;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.codehaus.groovy.util.FastArray;
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
@RequestMapping("/admin/course")
public class CourseAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

    @Autowired
    MailService mailService;

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseService courseService;

    @Autowired
    private CourseSectionService courseSectionService;

    @Autowired
    private CourseFileService courseFileService;

    @Autowired
    private CourseFileService fileService;

    @Autowired
    private UserMapperService userMapperService;

    private PageInfo pageInfo = new PageInfo();

    private CourseMaster courseMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");

        //courseMaster = courseMasterService.getById("A01");
    }


    // region # 공지사항
    @GetMapping("/list/{typeId}")
    public String noticeListMulti(@PathVariable("typeId") String typeId, @PageableDefault Pageable pageable, Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //courseService.deleteBlankCourse();

        String courseName = courseMasterService.getById(typeId).getCourseName();
        pageInfo.setPageTitle(courseName + "조회");

        Page<Course> courses = courseService.getPageList(typeId, pageable);
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);
        model.addAttribute("typeId", typeId);

        return "admin/course/list";
    }

    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);
        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "admin/course/view";
    }

    @GetMapping("/add/{typeId}")
    public String noticeAdd(@PathVariable("typeId") String typeId, Model model) {

        this.courseMaster = courseMasterService.getById(typeId);
        Course course = new Course();
        course.setCourseMaster(this.courseMaster);

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(courseMaster.getCourseName() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", typeId);
        model.addAttribute("mailList", userMapperService.getUserAll());

        return "admin/course/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid Course course
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/add";
        }

        course.setCourseMaster(courseMasterService.getById(course.getCourseMaster().getId()));

        boolean isMail = mails[0].equals("0") ? false : true;

        if (isMail) {
            course.setMailSender(Arrays.toString(mails));
        }
        Course course1 = courseService.save(course);

        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());

        if(course1.getCourseMaster().getIsMail().equals("Y") && isMail) {

            for(String userId : mails) {

                String email = userMapperService.getUserById(userId).getEmail();

                // 메일보내기(서비스 전까지는 메일 보내지 않음
                Mail mail = new Mail();
                //mail.setEmail(email);
                mail.setEmail("ks.hwang@safesoft.co.kr");
                mail.setMessage(course1.getContent());
                mail.setObject(course1.getTitle());

                mailService.send(mail);
            }
        }

        return "redirect:/admin/course/edit/" + course1.getId();
    }

    // 첨부파일 업로드
//    @PostMapping("/file-post")
//    public String filePost(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
//
//        Course course = courseService.getCourseById(id);
//
//        courseSectionService.storeFile(file, course);
//
//        return "redirect:/admin/course/list/" + course.getCourseMaster().getId();
//    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Course course = courseService.getCourseById(id);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", course.getId());

        return "admin/course/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Course course
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + course.getCourseMaster().getId();
        }

        List<CourseFile> courseFile= courseService.getCourseById(id).getCourseFiles();
        course.setCourseFiles(courseFile);

        Course course1 = courseService.save(course);

        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());

        return "redirect:/admin/course/edit/" + id;
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Course course = courseService.getCourseById(id);

        String courseMastId = course.getCourseMaster().getId();

        courseService.delete(course);

        return "redirect:/admin/course/list/" + courseMastId;
    }


    @GetMapping("/delete-file/{course_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long course_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/course/edit/" + course_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseFile courseSection = fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseSection.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseSection.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseSection.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +  newFileName + "\"")
                .body(resource);
    }



}
