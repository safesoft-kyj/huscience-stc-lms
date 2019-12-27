package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.ScheduleFile;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.service.ScheduleFileService;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.DateUtil;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/schedule")
//@SessionAttributes({"schedule"})
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleFileService fileService;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    public ScheduleController() {
        pageInfo.setParentId("m-basecode");
        pageInfo.setParentTitle("교육과정기준정보");
    }

    @GetMapping("/{sctype}")
    public String scList(@PathVariable("sctype") ScheduleType sctype, Model model) {

        if (sctype == ScheduleType.MATRIX) {
            pageInfo.setPageTitle("Employee Matrix");
        }
        else if (sctype == ScheduleType.CALENDAR) {
            pageInfo.setPageTitle("연간일정");
        }


        List<Schedule> borders = scheduleService.getListBySctypeOrderByCreatedDateDesc(sctype);
        model.addAttribute(pageInfo);
        model.addAttribute("sctype", sctype);
        model.addAttribute("borders", borders);

        return "admin/schedule/list";
    }

    @GetMapping("/{sctype}/view")
    public String scView(@PathVariable("sctype") ScheduleType sctype
            , @RequestParam("id") long id
            , Model model) {

        Schedule schedule = scheduleService.getById(id);
        //schedule.setViewCnt(schedule.getViewCnt() + 1);
        scheduleService.updateViewCnt(id, SessionUtil.getUserId());


        Schedule border1= scheduleService.save(schedule);

        pageInfo.setPageTitle("연간일정");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border1);

        return "admin/schedule/view/" + sctype;
    }

    @GetMapping("/{sctype}/updateActive")
    public String updateActive(@RequestParam("id") long id) {

        // 요청된 설문을 기본 설문으로 변경한다.
        Schedule schedule = scheduleService.getById(id);

        // 일정을 초기화 한다.
        for(Schedule tmpSchedule : scheduleService.getListBySctypeOrderByCreatedDateDesc(schedule.getSctype())) {
            tmpSchedule.setIsActive(0);
            scheduleService.save(tmpSchedule);
        }

//        if (schedule.getIsActive() == 0) {
//            // 교육과정을 신청할 수 있는 상태로 변경한다.
//            schedule.setIsActive(1);
//        } else if (schedule.getIsActive() == 1) {
//
//            // 직원이 신청한 내역이 있으면 Active를 변경할 수 없다.
////            if(course.getCourseAccountList().size() <= 0) {
////                course.setActive(0);
////            }
//
//            schedule.setIsActive(0);
//        }

        schedule.setIsActive(1);
        scheduleService.save(schedule);

        return "redirect:/admin/schedule/" + schedule.getSctype();
    }

    @GetMapping("/{sctype}/add")
    public String scAdd(@PathVariable("sctype") ScheduleType sctype, Model model) {

        Schedule schedule = new Schedule();
        schedule.setSctype(sctype);

        Calendar now = Calendar.getInstance();
        schedule.setYear(String.valueOf(now.get(Calendar.YEAR)));

        pageInfo.setPageTitle("연간일정");

        model.addAttribute(pageInfo);
        model.addAttribute("schedule", schedule);
        model.addAttribute("sctype", sctype);
        model.addAttribute("yearList", DateUtil.getYearList());

        return "admin/schedule/add";
    }

    @PostMapping("/{sctype}/add-post")
    public String scAddPost(@Valid Schedule schedule
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/schedule/add/" + schedule.getSctype();
        }

        Schedule schedule1 = scheduleService.save(schedule);

        // Section 저장
        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, schedule1))
                .collect(Collectors.toList());

//        status.setComplete();
        return "redirect:/admin/schedule/" + schedule1.getSctype();
    }

    @GetMapping("/{sctype}/edit")
    public String noticeEdit(@PathVariable("sctype") ScheduleType sctype
            , @RequestParam("id") long id
            , Model model) {

        pageInfo.setPageId("m-border-edit");
        pageInfo.setPageTitle("연간일정");

        Schedule schedule = scheduleService.getById(id);

        model.addAttribute(pageInfo);
        model.addAttribute("sctype", sctype);
        model.addAttribute("yearList", DateUtil.getYearList());
        model.addAttribute("schedule", schedule);

        return "admin/schedule/edit";
    }

    @PostMapping("/{sctype}/edit-post/{id}")
    public String noticeEditPost(@PathVariable("sctype") ScheduleType sctype
            , @PathVariable("id") long id, @Valid Schedule schedule
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            schedule.setId(id);
            return "notice-edit";
        }

        Schedule oldSchedule = scheduleService.getById(id);
        List<ScheduleFile>  scheduleFiles= oldSchedule.getScheduleFiles();
        schedule.setScheduleFiles(scheduleFiles);
        schedule.setIsActive(oldSchedule.getIsActive());
        schedule.setScheduleViewAccounts(oldSchedule.getScheduleViewAccounts());
        schedule.setViewCnt(oldSchedule.getViewCnt());

        Schedule schedule1 = scheduleService.save(schedule);

        // Section 저장
        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, schedule1))
                .collect(Collectors.toList());

        return "redirect:/admin/schedule/" + schedule1.getSctype();
    }

    @GetMapping("/{sctype}/delete/{id}")
    public String noticeDelete(@PathVariable("sctype") ScheduleType sctype, @PathVariable("id") long id, HttpServletRequest request) {

        Schedule schedule = scheduleService.getById(id);

        scheduleService.delete(schedule);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }

    @GetMapping("/{sctype}/delete-file/{schedule_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable("sctype") ScheduleType sctype
            , @PathVariable long schedule_id
            , @PathVariable long file_id
            , HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;

    }

    @GetMapping("/{sctype}/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        ScheduleFile file =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(file.getSaveName());

        // Try to determine file's content type
//        String contentType = mimeTypesMap.getContentType(file.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        String contentType = file.getMimeType();

        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, file.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + newFileName + "\"")
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }


}