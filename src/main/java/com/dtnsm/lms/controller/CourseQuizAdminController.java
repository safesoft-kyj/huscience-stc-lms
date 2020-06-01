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
@RequestMapping("/admin/course")
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
        pageInfo.setParentTitle("교육과정");
    }

    @GetMapping("/{typeId}/{courseId}/quiz")
    public String list(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "시험"));

        // 설문조사 여부가 Y 인경우만 Add 버튼 활성화
        String isAdd = course.getIsQuiz();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", quizService.getAllByCourseId(courseId));
        model.addAttribute("isAdd", isAdd);
        model.addAttribute("typeId", course.getCourseMaster().getId());
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/quiz/list";
    }

    @GetMapping("/{typeId}/{courseId}/quiz/add")
    public String noticeAdd(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);
        CourseQuiz courseQuiz = new CourseQuiz();
        courseQuiz.setCourse(course);

        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "시험"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/quiz/add";
    }

    @PostMapping("/{typeId}/{courseId}/quiz/add-post")
    public String noticeAddPost(@Valid CourseQuiz quiz
            , @PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
//            , @RequestParam("id") Long id
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , @RequestParam("file") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + courseId;
        }

        Course course = courseService.getCourseById(courseId);
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

        return String.format("redirect:/admin/course/%s/%s/quiz?page=%s"
                , quiz.getCourse().getCourseMaster().getId()
                , quiz.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/quiz/edit")
    public String noticeEdit(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId, @RequestParam("id") long id, Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "시험"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
//        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseQuiz.getId());
        model.addAttribute("typeId", typeId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", courseQuiz.getCourse().getTitle());

        return "admin/course/quiz/edit";
    }

    @PostMapping("/{typeId}/{courseId}/quiz/edit-post/{id}")
    public String noticeEditPost(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
//            , @PathVariable("id") long id
            , @Valid CourseQuiz courseQuiz
            , @RequestParam("file") MultipartFile file
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(courseQuiz.getId());
            return "/admin/course/list/" + courseId;
        }

        CourseQuiz courseQuizOld = quizService.getCourseQuizById(courseQuiz.getId());

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

        return String.format("redirect:/admin/course/%s/%s/quiz?page=%s"
                , courseQuiz1.getCourse().getCourseMaster().getId()
                , courseQuiz1.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/quiz/delete/{id}")
    public String noticeDelete(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , @PathVariable("id") long id, HttpServletRequest request) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
//        if (courseQuiz.getCourse().getCourseAccountList().size() <= 0) {

            // 문제 삭제
            for(CourseQuizQuestion courseQuizQuestion : courseQuiz.getQuizQuestions()) {
                quizService.deleteQuizQuestion(courseQuizQuestion);
            }
            // 첨부파일 삭제
            for(CourseQuizFile courseQuizFile : courseQuiz.getQuizFiles()) {
                fileService.deleteFile(courseQuizFile);
            }
            // 시험 삭제
            quizService.deleteQuiz(courseQuiz);
//        }

        // 이전 URL를 리턴한다.
//        String refUrl = request.getHeader("referer");
//        return "redirect:" +  refUrl;

        return String.format("redirect:/admin/course/%s/%s/quiz?page=%s"
                , typeId
                , courseId
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/quiz/delete-file/{quiz_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable long quiz_id
            , @PathVariable long file_id
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , HttpServletRequest request){


//        CourseQuiz courseQuiz = quizService.getCourseQuizById(quiz_id);

        List<CourseQuizQuestion> courseQuizQuestions =  quizService.getAllByQuizId(quiz_id);

        // 문제 삭제
        for(CourseQuizQuestion courseQuizQuestion : courseQuizQuestions) {
            quizService.deleteQuizQuestion(courseQuizQuestion);
        }

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }

    @GetMapping("/{typeId}/{courseId}/quiz/view")
    public String viewPage(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("quizId") long quizId
            , Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(quizId);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "시험"));

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseQuiz.getQuizQuestions());
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", courseQuiz.getCourse().getTitle());

        return "admin/course/quiz/view";
    }

    @GetMapping("/{typeId}/{courseId}/quiz/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable long id, HttpServletRequest request){

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
    @GetMapping("/{typeId}/{courseId}/quiz/report")
    public String reportPage(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("quizId") long quizId
            , Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(quizId);

        // 문제 오답 통계 쿼리
        List<QuizReportForm1> questionList = reportMapperService.getQuizReport(courseQuiz.getCourse().getId(), quizId);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "시험"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
        model.addAttribute("questionList", questionList);
        model.addAttribute("courseTitle", courseQuiz.getCourse().getTitle());
        model.addAttribute("courseName", courseQuiz.getCourse().getTitle());

        return "admin/course/quiz/report";
    }
}
