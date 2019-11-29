package com.dtnsm.lms.controller;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.dto.QuizReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm3;
import com.dtnsm.lms.mybatis.service.ReportMapperService;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
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
import java.util.ArrayList;
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
    private ReportMapperService reportMapperService;

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
        pageInfo.setParentTitle("과정시험정보");
    }

    @GetMapping("/list/{courseId}")
    public String list(@PathVariable("courseId") Long courseId, Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("시험조회");

        course = courseService.getCourseById(courseId);
        // 설문조사 여부가 Y 인경우만 Add 버튼 활성화
        String isAdd = course.getIsQuiz();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", quizService.getAllByCourseId(courseId));
        model.addAttribute("isAdd", isAdd);
        model.addAttribute("typeId", course.getCourseMaster().getId());

        return "admin/course/quiz/list";
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
        quiz.setMinute(Math.round(quiz.getHour() * 60));
        quiz.setSecond(Math.round(quiz.getMinute() * 60));

        // Section 저장
        CourseQuiz quiz1 = quizService.saveQuiz(quiz);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            CourseQuizFile uploadFile = fileService.storeFile(file, quiz1);

            try {
                List<CourseQuizQuestion> courseQuizQuestions = excelReader.readFileToList(file, CourseQuizQuestion::fromQuiz);

                for (CourseQuizQuestion quizQuestion : courseQuizQuestions) {
                    // 시험문제와 연결한다.

                    if (!quizQuestion.getQuestion().isEmpty()) {
                        quizQuestion.setQuiz(quiz1);
                        quizService.saveQuizQuestion(quizQuestion);
                    }
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

        CourseQuiz courseQuizOld = quizService.getCourseQuizById(id);

        Course course = courseQuizOld.getCourse();
        courseQuiz.setCourse(course);
        courseQuiz.setQuizFiles(courseQuizOld.getQuizFiles());
        courseQuiz.setQuizActions(courseQuizOld.getQuizActions());
        courseQuiz.setQuizQuestions(courseQuizOld.getQuizQuestions());
        courseQuiz.setMinute(Math.round(courseQuiz.getHour() * 60));
        courseQuiz.setSecond(Math.round(courseQuiz.getMinute() * 60));

        CourseQuiz courseQuiz1 = quizService.saveQuiz(courseQuiz);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            CourseQuizFile uploadFile = fileService.storeFile(file, courseQuiz1);

            try {
                List<CourseQuizQuestion> courseQuizQuestions = excelReader.readFileToList(file, CourseQuizQuestion::fromQuiz);

                for (CourseQuizQuestion quizQuestion : courseQuizQuestions) {
                    // 시험문제와 연결한다.
                    if (!quizQuestion.getQuestion().isEmpty()) {
                        quizQuestion.setQuiz(courseQuiz1);
                        quizService.saveQuizQuestion(quizQuestion);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/course/quiz/list/" + courseQuiz1.getCourse().getId();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        Long courseId = courseQuiz.getCourse().getId();

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (courseQuiz.getCourse().getCourseAccountList().size() <= 0) {
            quizService.deleteQuiz(courseQuiz);
        }

        return "redirect:/admin/course/quiz/list/" + courseId;
    }

    @GetMapping("/delete-file/{quiz_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long quiz_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/course/quiz/edit/" + quiz_id;

    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(courseQuiz.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseQuiz.getQuizQuestions());
        model.addAttribute("courseId", courseQuiz.getCourse().getId());

        return "admin/course/quiz/view";
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

    // Report
    @GetMapping("/report/{id}")
    public String reportPage(@PathVariable("id") long quizId, Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(quizId);

        // 문제 오답 통계 쿼리
        List<QuizReportForm1> questionList = reportMapperService.getQuizReport(courseQuiz.getCourse().getId(), quizId);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(courseQuiz.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
        model.addAttribute("questionList", questionList);

        return "admin/course/quiz/report";
    }
}
