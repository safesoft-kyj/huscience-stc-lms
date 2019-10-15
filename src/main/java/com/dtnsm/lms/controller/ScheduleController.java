package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/admin/info/year")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    private PageInfo pageInfo = new PageInfo();

    public ScheduleController() {
        pageInfo.setParentId("m-basecode");
        pageInfo.setParentTitle("연간일정");
    }

    @GetMapping("/list")
    public String scList(Model model) {

        pageInfo.setPageTitle("연간일정조회");

        List<Schedule> borders = scheduleService.getList();
        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);

        return "admin/info/year/list";
    }

    @GetMapping("/view/{id}")
    public String scView(@PathVariable("id") long id, Model model) {

        Schedule schedule = scheduleService.getScheduleById(id);
        schedule.setViewCnt(schedule.getViewCnt() + 1);

        Schedule border1= scheduleService.save(schedule);

        pageInfo.setPageTitle("연간일정상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border1);

        return "admin/info/year/view";
    }

    @GetMapping("/add")
    public String scAdd(Model model) {

        Schedule schedule = new Schedule();

        Calendar now = Calendar.getInstance();
        schedule.setYear(String.valueOf(now.get(Calendar.YEAR)));

        pageInfo.setPageTitle("연간일정등록");
        model.addAttribute(pageInfo);
        model.addAttribute("schedule", schedule);
        model.addAttribute("yearList", DateUtil.getYearList());

        return "admin/info/year/add";
    }

    @PostMapping("/add-post")
    public String scAddPost(@Valid Schedule schedule, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/info/year/add";
        }

        scheduleService.save(schedule);

        return "redirect:/admin/info/year/list/";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        pageInfo.setPageId("m-border-edit");
        pageInfo.setPageTitle("연간일정수정");
        model.addAttribute(pageInfo);
        model.addAttribute("yearList", DateUtil.getYearList());
        model.addAttribute("border", scheduleService.getScheduleById(id));

        return "/admin/info/year/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id, @Valid Schedule schedule, BindingResult result) {
        if(result.hasErrors()) {
            schedule.setId(id);
            return "notice-edit";
        }
        scheduleService.save(schedule);

        return "redirect:/admin/info/year/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Schedule schedule = scheduleService.getScheduleById(id);

        scheduleService.delete(schedule);

        return "redirect:/admin/info/year/list";
    }
}