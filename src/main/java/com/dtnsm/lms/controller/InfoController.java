package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/info")
public class InfoController {

    @Autowired
    CourseService courseService;

    @Autowired
    ScheduleService scheduleService;

    private PageInfo pageInfo = new PageInfo();

    public InfoController() {
        pageInfo.setParentId("m-info");
        pageInfo.setParentTitle("교육안내");
    }

    @GetMapping("/requestMonth")
    public String requestMonth(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정(신청일기준)");
        model.addAttribute(pageInfo);

        return "content/info/requestMonth";
    }

    @GetMapping("/month")
    public String month(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정(교육일기준)");
        model.addAttribute(pageInfo);

        return "content/info/month";
    }

    @GetMapping("/year")
    public String year(@RequestParam(value = "year", defaultValue = "n") String year, Model model) {

        pageInfo.setPageId("m-info-year");
        pageInfo.setPageTitle("교육연간일정");

        if (year.equals("n")) {
            Calendar now = Calendar.getInstance();
            year = String.valueOf(now.get(Calendar.YEAR));
        }

        Schedule schedule = scheduleService.getScheduleTop1ByYear(year);

        List<String> yearList = new ArrayList<>();
        for(Schedule schedule1 : scheduleService.getList()){
            yearList.add(schedule1.getYear());
        }


        model.addAttribute(pageInfo);
        model.addAttribute("border", schedule);
        model.addAttribute("yearList", yearList);

        return "content/info/year";
    }


    // 교육 신청
    @GetMapping("/request")
    public String noticeListMulti(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-info-request");
        pageInfo.setPageTitle("교육신청");

        Page<Course> courses = courseService.getPageList(pageable);



        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);

        return "content/info/request";
    }

}
