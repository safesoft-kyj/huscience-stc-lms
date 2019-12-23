package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseSectionFile;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.ScheduleFile;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/info")
public class InfoController {

    @Autowired
    CourseService courseService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    ScheduleFileService fileService;

    @Autowired
    CodeService codeService;

    private PageInfo pageInfo = new PageInfo();

    public InfoController() {
        pageInfo.setParentId("m-info");
        pageInfo.setParentTitle("교육안내");
    }

    @GetMapping("/requestMonth")
    public String requestMonth(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정(신청일)");
        model.addAttribute(pageInfo);

        return "content/info/requestMonth";
    }


    // 신청일 + 교육일 보기
    @GetMapping("/totalMonth")
    public String month(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정");
        model.addAttribute(pageInfo);

        return "content/info/totalMonth";
    }

    @GetMapping("/month")
    public String totalMonth(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정(교육일)");
        model.addAttribute(pageInfo);

        return "content/info/month";
    }

    @GetMapping("/requestMonthUser")
    public String requestMonthUser(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("나의월간일정(신청일)");
        model.addAttribute(pageInfo);

        return "content/info/requestMonthUser";
    }

    @GetMapping("/monthUser")
    public String monthUser(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("나의월간일정(교육일)");
        model.addAttribute(pageInfo);

        return "content/info/monthUser";
    }

    @GetMapping("/year/{sctype}")
    public String year(@RequestParam(value = "title", defaultValue = "") String title
            , @PathVariable("sctype") ScheduleType sctype
            , Model model) {

        pageInfo.setPageId("m-info-year");
        pageInfo.setPageTitle("교육연간일정");

        Schedule schedule = scheduleService.getByIsActive(sctype, 1);

        if (schedule != null) {

            List<String> titleList = new ArrayList<>();

            ScheduleFile scheduleFile = null;

            if (schedule.getScheduleFiles().size() > 0) scheduleFile = schedule.getScheduleFiles().get(0);

            for (Schedule schedule1 : scheduleService.getListBySctypeOrderByCreatedDateDesc(sctype)) {
                titleList.add(schedule1.getTitle());
            }

            // 읽음 증가
            scheduleService.updateViewCnt(schedule.getId(), SessionUtil.getUserId());

            model.addAttribute("scheduleFileId", scheduleFile.getId());
//            model.addAttribute("titleList", titleList);
        } else {
            model.addAttribute("scheduleFileId", "");
        }

        model.addAttribute(pageInfo);
        model.addAttribute("border", schedule);
        model.addAttribute("currTitle", title);

        return "content/info/year";
    }


    // 교육 신청
    @GetMapping("/request")
    public String noticeListMulti(@RequestParam(value = "typeId", defaultValue = "") String typeId
            , @RequestParam(value = "gubunId", defaultValue = "") String gubunId
            , @RequestParam(value = "title", defaultValue = "") String title
            , @PageableDefault Pageable pageable
            , Model model) {

        pageInfo.setPageId("m-info-request");
        pageInfo.setPageTitle("교육신청");

//        Page<Course> courses;
//        if( gubunId.equals("all") && typeId.equals("all") && title.equals("")) {
//            courses = courseService.getPageList(0, pageable);
//        } else if ( gubunId.equals("all") && typeId.equals("all") && !title.equals("") ){
//            courses = courseService.getPageListByTitleLike(title, 0, pageable);
//        } else if (!typeId.equals("all") && title.equals("") ){
//            courses = courseService.getPageLisByTypeId(typeId, 0, pageable);
//        } else {
//            courses = courseService.getPageLisByTypeIdAndTitleLike(typeId, title, 0, pageable);
//        }

        Page<Course> courses = courseService.getAllByUserRequest(gubunId, typeId, title,1, pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);
        model.addAttribute("courseMasterList", courseMasterService.getList());
        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine 구분

        return "content/info/request";
    }

    // 교육 신청
    @GetMapping("/request/{typeId}")
    public String requestCourseTypeId(@PathVariable("typeId") String typeId, @PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-info-request");
        pageInfo.setPageTitle("교육신청");

        Page<Course> courses = courseService.getPageLisByTypeId(typeId, 0, pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);
        model.addAttribute("courseMasterList", courseMasterService.getList());

        return "content/info/request";
    }

    // employee metrix 보기
    @GetMapping("/matrix/pdfview/{id}")
    public String pdfView(@PathVariable("id") Long fileId, Model model) {

//        Schedule schedule =  scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX);

        Schedule schedule = scheduleService.getByIsActive(ScheduleType.MATRIX, 1);
        // 읽음 증가
        scheduleService.updateViewCnt(schedule.getId(), SessionUtil.getUserId());

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(schedule.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("scheduleFileList", schedule.getScheduleFiles());

        return "content/info/matrix";
    }

}
