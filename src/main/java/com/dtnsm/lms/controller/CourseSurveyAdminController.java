package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.dto.ReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm3;
import com.dtnsm.lms.mybatis.service.ReportMapperService;
import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.service.CourseSurveyService;
import com.dtnsm.lms.service.SurveyService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/course")
public class CourseSurveyAdminController {
    @Autowired
    CodeService codeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSurveyService courseSurveyService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    ReportMapperService reportMapperService;

    private PageInfo pageInfo = new PageInfo();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0303";

    private Course course;

    public CourseSurveyAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");
    }

    @GetMapping("/{typeId}/{courseId}/survey")
    public String list( @PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , Model model) {

        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s'>%s</a> > %s", typeId , course.getCourseMaster().getCourseName(), "설문"));

        // 설문조사 여부가 Y 인경우만 Add 버튼 활성화
        String isAdd = course.getIsSurvey();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSurveyService.getAllByCourseId(courseId));
        model.addAttribute("isAdd", isAdd);
        model.addAttribute("typeId", course.getCourseMaster().getId());
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/survey/list";
    }

    @GetMapping("/{typeId}/{courseId}/survey/add")
    public String noticeAdd(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);
        CourseSurvey courseSurvey = new CourseSurvey();
        courseSurvey.setCourse(course);

        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s'>%s</a> > %s", typeId , course.getCourseMaster().getCourseName(), "설문"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);
        model.addAttribute("surveyList", surveyService.getList());
        model.addAttribute("id", courseId);
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/survey/add";
    }

    @PostMapping("/{typeId}/{courseId}/survey/add-post")
    public String noticeAddPost(@Valid CourseSurvey courseSurvey
            , @PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("id") Long id
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        courseSurvey.setCourse(course);

        // Section 저장
        CourseSurvey courseSurvey1 = courseSurveyService.saveSurvey(courseSurvey);

        // 선택된 설문을 복사한다.
        courseSurveyService.CopySurveyQuestion(courseSurvey1);

        return String.format("redirect:/admin/course/%s/%s/survey?page=%s"
                , courseSurvey1.getCourse().getCourseMaster().getId()
                , courseSurvey1.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/survey/edit")
    public String noticeEdit(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("id") long id
            , Model model
            , RedirectAttributes attributes) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        Course course = courseService.getCourseById(courseId);

        if(course.getCourseAccountList().size() > 0) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "수강자가 있을 경우 설문을 변경할 수 없습니다.");
            return "redirect:/admin/course/" + typeId + "/" + courseId + "/survey";
        }

        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s'>%s</a> > %s", typeId , course.getCourseMaster().getCourseName(), "설문"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);
        model.addAttribute("surveyList", surveyService.getList());
        model.addAttribute("id", courseSurvey.getId());
        model.addAttribute("courseName", course.getTitle());

        return "admin/course/survey/edit";
    }

    @PostMapping("/{typeId}/{courseId}/survey/edit-post/{id}")
    public String noticeEditPost(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable("id") long id
            , @Valid CourseSurvey courseSurvey
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + course.getCourseMaster().getId();
        }

        CourseSurvey courseSurveyOld = courseSurveyService.getCourseSurveyById(id);

        Course course = courseSurveyOld.getCourse();
        courseSurvey.setCourse(course);

        CourseSurvey courseSurvey1 = courseSurveyService.saveSurvey(courseSurvey);

        // 선택된 설문을 복사한다.
        courseSurveyService.CopySurveyQuestion(courseSurvey1);


        return String.format("redirect:/admin/course/%s/%s/survey?page=%s"
                , courseSurvey1.getCourse().getCourseMaster().getId()
                , courseSurvey1.getCourse().getId()
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/survey/delete/{id}")
    public String noticeDelete(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , @PathVariable("id") long id, HttpServletRequest request) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (courseSurvey.getCourse().getCourseAccountList().size() <= 0) {
            courseSurveyService.deleteSurvey(courseSurvey);
        }

        // 이전 URL를 리턴한다.
//        String refUrl = request.getHeader("referer");
//        return "redirect:" +  refUrl;

        return String.format("redirect:/admin/course/%s/%s/survey?page=%s"
                , typeId
                , courseId
                , page);
    }

    @GetMapping("/{typeId}/{courseId}/survey/view")
    public String viewPage(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("surveyId") long surveyId, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(surveyId);

        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s'>%s</a> > %s", typeId , course.getCourseMaster().getCourseName(), "설문"));



        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSurvey.getQuestions());
        model.addAttribute("courseId", courseSurvey.getCourse().getId());
        model.addAttribute("courseName", courseSurvey.getCourse().getTitle());

        return "admin/course/survey/view";
    }

    // Report
    @GetMapping("/{typeId}/{courseId}/survey/report")
    public String reportPage(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam("surveyId") long surveyId, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(surveyId);

        boolean isSelf = true;
        if (courseSurvey.getCourse().getCourseMaster().getId().equals("BC0101")) {
            if (courseSurvey.getQuestions().size() > 1) {
                isSelf = false;
            }
        }

        List<ReportForm1> borders = reportMapperService.getSurveyReport(courseId);

        List<ReportForm3> shoutQuestionsAnswer = reportMapperService.getSurveyReport2(courseId);


        List<CourseSurveyQuestion> shoutQuestions = new ArrayList<>();
        for(CourseSurveyQuestion courseSurveyQuestion : courseSurvey.getQuestions()) {
            if (courseSurveyQuestion.getSurveyGubun().equals("S")) {
                shoutQuestions.add(courseSurveyQuestion);
            }
        }

        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s'>%s</a> > %s", typeId , course.getCourseMaster().getCourseName(), "설문"));

        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);
        model.addAttribute("shoutQuestions", shoutQuestions);
        model.addAttribute("shoutQuestionsAnswer", shoutQuestionsAnswer);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", courseSurvey.getName().isEmpty() ? courseSurvey.getCourse().getTitle() : courseSurvey.getName());
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("courseName", courseSurvey.getCourse().getTitle());

        return "admin/course/survey/report";
    }

}
