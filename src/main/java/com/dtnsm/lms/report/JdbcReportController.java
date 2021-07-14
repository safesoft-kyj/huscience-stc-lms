package com.dtnsm.lms.report;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-28 오후 3:22
 * @desc Github : https://github.com/pub147
 **/
@Slf4j
@RequestMapping("/admin/report")
@Controller
public class JdbcReportController {

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseReportRepository courseReportRepository;

    @Autowired
    private TrainingLogReportRepository trainingLogReportRepository;

    private final PageInfo pageInfo = new PageInfo();

    public JdbcReportController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("Report");
    }

    @GetMapping("/training/head")
    public String trainingHead(Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findLogByUser("%");

        pageInfo.setPageTitle("Training Summary");

        model.addAttribute(pageInfo);
        model.addAttribute("logList", logList);

        return "admin/report/training-head";
    }

    @GetMapping({"/training/detail", "/training/detail/{userId}"})
    public String trainingDetail(@RequestParam(value = "userName", defaultValue = "%", required = false) String userName
            , @PageableDefault Pageable pageable, Model model) {

        Page<Map<String, Object>> logList = trainingLogReportRepository.findLogDetailByUser(userName, pageable);
        pageInfo.setPageTitle("Training Log");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", logList);

        return "admin/report/training-detail";
    }

    @GetMapping("/certificate/head")
    public String certificateHead(Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findCertByUser("%");

        pageInfo.setPageTitle("수료증 Summary");

        model.addAttribute(pageInfo);
        model.addAttribute("logList", logList);

        return "admin/report/certificate-head";
    }

    @GetMapping({"/certificate/detail", "/certificate/detail/{userId}"})
    public String certificateDetail(@RequestParam(value = "userName", defaultValue = "%", required = false) String userName
            , @PageableDefault Pageable pageable, Model model) {

        Page<Map<String, Object>> logList = trainingLogReportRepository.findCertDetailByUser(userName, pageable);
        pageInfo.setPageTitle("수료증");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", logList);

        return "admin/report/certificate-detail";
    }

    @GetMapping("/bind/target")
    public String bindTarget(Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findBindTargetList();

        pageInfo.setPageTitle("Bind Target");

        model.addAttribute(pageInfo);
        model.addAttribute("logList", logList);

        return "admin/report/bind-target";
    }

    @GetMapping("/bind/target/process")
    public String bindTarget(@RequestParam("docId") Long docId) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        courseAccountService.courseAccountManualCommit(courseAccount);

        return "redirect:/admin/report/bind/target";
    }

    /**
     * 부서별 사용자별 Self Training 상태를 확인한다.
     * @param
     * @return
     * @exception
     * @see
     */
    @RequestMapping({"/course/self", "/{employees}/report"})
    public String courseList(Model model
            , @PathVariable(value = "employees", required = false) String employees
            , @RequestParam(value = "courseTitle", defaultValue = "%") String courseTitle
            , @RequestParam(value = "orgDepart", defaultValue = "%") String ordDepart
            , @RequestParam(value = "orgTeam", defaultValue = "%") String orgTeam
            , @RequestParam(value = "status", defaultValue = "0") String status
            , @RequestParam(value = "userName", defaultValue = "%") String userName
            , @PageableDefault Pageable pageable) {

        if(StringUtils.isEmpty(employees)) {
            pageInfo.setPageTitle("Self Training 교육현황");
        } else {
            pageInfo.setParentId("m-teamDept");
            pageInfo.setParentTitle("Team/Department");

            pageInfo.setPageId("m-report");
            pageInfo.setPageTitle("Report");
        }
        model.addAttribute(pageInfo);

        Page<Map<String, Object>> mapPage;
        if(StringUtils.isEmpty(employees))
            mapPage = courseReportRepository.findBySelfTraining(courseTitle, ordDepart, orgTeam, userName, status, pageable);
        else
            mapPage = courseReportRepository.findByParentUser(courseTitle, ordDepart, orgTeam, userName, status, pageable);

        model.addAttribute("borders", mapPage);

        return "admin/report/course-self";
    }
}
