package com.dtnsm.lms.controller;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.service.CourseQuizFileService;
import com.dtnsm.lms.service.CourseQuizService;
import com.dtnsm.lms.service.CourseService;
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
import java.util.List;

@Controller
@RequestMapping("/admin/course/quiz")
public class CourseQuizAdminController {

    @Autowired
    CodeService codeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseQuizService quizService;

    @Autowired
    private CourseQuizFileService fileService;

    @Autowired
    ExcelReader excelReader;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0302";


    private Course course;

    public CourseQuizAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");
    }

    @GetMapping("/add/{courseId}")
    public String noticeAdd(@PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);
        CourseQuiz courseQuiz = new CourseQuiz();
        courseQuiz.setCourse(course);

        pageInfo.setPageTitle(course.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseId);

        return "admin/course/quiz/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseQuiz quiz
            , @RequestParam("id") Long id
            , @RequestParam("file") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        quiz.setCourse(course);
        quiz.setSecond(quiz.getMinute()*60);

        // Section 저장
        CourseQuiz quiz1 = quizService.saveQuiz(quiz);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            CourseQuizFile uploadFile = fileService.storeFile(file, quiz1);

            try {
                List<CourseQuizQuestion> courseQuizQuestions = excelReader.readFileToList(file, CourseQuizQuestion::fromQuiz);

                for (CourseQuizQuestion quizQuestion : courseQuizQuestions) {
                    // 시험문제와 연결한다.
                    quizQuestion.setQuiz(quiz1);
                    quizService.saveQuizQuestion(quizQuestion);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/course/list/" + quiz1.getCourse().getCourseMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        pageInfo.setPageTitle(courseQuiz.getName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseQuiz.getId());

        return "admin/course/quiz/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid CourseQuiz courseQuiz
            , @RequestParam("file") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + courseQuiz.getCourse().getId();
        }

        CourseQuiz courseSurveyOld = quizService.getCourseQuizById(id);

        Course course = courseSurveyOld.getCourse();
        courseQuiz.setCourse(course);
        courseQuiz.setQuizFiles(courseSurveyOld.getQuizFiles());
        courseQuiz.setSecond(courseQuiz.getMinute()*60);

        CourseQuiz courseQuiz1 = quizService.saveQuiz(courseQuiz);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            CourseQuizFile uploadFile = fileService.storeFile(file, courseQuiz1);

            try {
                List<CourseQuizQuestion> courseQuizQuestions = excelReader.readFileToList(file, CourseQuizQuestion::fromQuiz);

                for (CourseQuizQuestion quizQuestion : courseQuizQuestions) {
                    // 시험문제와 연결한다.
                    quizQuestion.setQuiz(courseQuiz1);
                    quizService.saveQuizQuestion(quizQuestion);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/course/view/" + courseQuiz1.getCourse().getId();
    }

    @GetMapping("/delete-file/{quiz_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long quiz_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/course/quiz/edit/" + quiz_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseQuizFile courseQuizFile =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseQuizFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseQuizFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseQuizFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}
