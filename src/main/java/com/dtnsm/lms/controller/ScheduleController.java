package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.ScheduleFile;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.service.ScheduleFileService;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.DateUtil;
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
        pageInfo.setParentTitle("연간일정");
    }

    @GetMapping("/list/{sctype}")
    public String scList(@PathVariable("sctype") ScheduleType sctype, Model model) {

        pageInfo.setPageTitle("연간일정조회");

        List<Schedule> borders = scheduleService.getListBySctypeOrderByCreatedDateDesc(sctype);
        model.addAttribute(pageInfo);
        model.addAttribute("sctype", sctype);
        model.addAttribute("borders", borders);

        return "admin/schedule/list";
    }

    @GetMapping("/view/{sctype}/{id}")
    public String scView(@PathVariable("sctype") ScheduleType sctype
            , @PathVariable("id") long id
            , Model model) {

        Schedule schedule = scheduleService.getById(id);
        schedule.setViewCnt(schedule.getViewCnt() + 1);

        Schedule border1= scheduleService.save(schedule);

        pageInfo.setPageTitle("연간일정상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border1);

        return "admin/schedule/view/" + sctype;
    }

    @GetMapping("/add/{sctype}")
    public String scAdd(@PathVariable("sctype") ScheduleType sctype, Model model) {

        Schedule schedule = new Schedule();
        schedule.setSctype(sctype);

        Calendar now = Calendar.getInstance();
        schedule.setYear(String.valueOf(now.get(Calendar.YEAR)));

        pageInfo.setPageTitle("연간일정등록");

        model.addAttribute(pageInfo);
        model.addAttribute("schedule", schedule);
        model.addAttribute("yearList", DateUtil.getYearList());

        return "admin/schedule/add";
    }

    @PostMapping("/add-post")
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
        return "redirect:/admin/schedule/list/" + schedule1.getSctype();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id
            , Model model) {

        pageInfo.setPageId("m-border-edit");
        pageInfo.setPageTitle("연간일정수정");

        Schedule schedule = scheduleService.getById(id);

        model.addAttribute(pageInfo);
        model.addAttribute("yearList", DateUtil.getYearList());
        model.addAttribute("schedule", schedule);

        return "/admin/schedule/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id, @Valid Schedule schedule
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            schedule.setId(id);
            return "notice-edit";
        }

        List<ScheduleFile>  scheduleFiles= scheduleService.getById(id).getScheduleFiles();
        schedule.setScheduleFiles(scheduleFiles);

        Schedule schedule1 = scheduleService.save(schedule);

        // Section 저장
        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file, schedule1))
                .collect(Collectors.toList());

        return "redirect:/admin/schedule/list/" + schedule1.getSctype();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Schedule schedule = scheduleService.getById(id);

        scheduleService.delete(schedule);

        return "redirect:/admin/schedule/list/CALENDAR";
    }

    @GetMapping("/delete-file/{schedule_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long schedule_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/schedule/edit/" + schedule_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        ScheduleFile file =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(file.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(file.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, file.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}