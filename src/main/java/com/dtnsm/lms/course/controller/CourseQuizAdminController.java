package com.dtnsm.lms.course.controller;

import com.dtnsm.lms.base.ElCodeService;
import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseQuiz;
import com.dtnsm.lms.course.domain.CourseSection;
import com.dtnsm.lms.course.domain.CourseSurvey;
import com.dtnsm.lms.course.service.CourseQuizService;
import com.dtnsm.lms.course.service.CourseService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/course/quiz")
public class CourseQuizAdminController {

    @Autowired
    ElCodeService codeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseQuizService quizService;

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
        courseQuiz.setType(codeService.getMinorById(minorCode));

        pageInfo.setPageTitle(course.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseId);

        return "admin/course/quiz/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseQuiz quiz
            , @RequestParam("id") Long id
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        quiz.setCourse(course);

        // Section 저장
        CourseQuiz quiz1 = quizService.saveQuiz(quiz);

        return "redirect:/admin/course/list/" + quiz1.getCourse().getCourseMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseQuiz courseQuiz = quizService.getCourseQuizById(id);

        pageInfo.setPageTitle(courseQuiz.getName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuiz", courseQuiz);
        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseQuiz.getId());

        return "admin/course/quiz/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid CourseQuiz courseQuiz
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + courseQuiz.getCourse().getId();
        }

        CourseQuiz courseSurveyOld = quizService.getCourseQuizById(id);

        Course course = courseSurveyOld.getCourse();
        courseQuiz.setCourse(course);

        CourseQuiz courseQuiz1 = quizService.saveQuiz(courseQuiz);

        return "redirect:/admin/course/view/" + courseQuiz1.getCourse().getId();
    }
}
