package com.dtnsm.lms.report;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.domain.DTO.SelfTrainingList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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
        model.addAttribute("totalSize", mapPage.getTotalElements());

        return "admin/report/course-self";
    }

    @RequestMapping("/excel")
    @Transactional(readOnly = true)
    public void downloadSelfTrainingList(Model model
            , @PathVariable(value = "employees", required = false) String employees
            , @RequestParam(value = "courseTitle", defaultValue = "%") String courseTitle
            , @RequestParam(value = "orgDepart", defaultValue = "%") String ordDepart
            , @RequestParam(value = "orgTeam", defaultValue = "%") String orgTeam
            , @RequestParam(value = "status", defaultValue = "0") String status
            , @RequestParam(value = "userName", defaultValue = "%") String userName
            , HttpServletResponse response) throws Exception {

       List<SelfTrainingList> selfTrainingList;
        if(StringUtils.isEmpty(employees))
            selfTrainingList = courseReportRepository.findBySelfTrainingAll(courseTitle, ordDepart, orgTeam, userName, status);
        else
            selfTrainingList = courseReportRepository.findByParentUserAll(courseTitle, ordDepart, orgTeam, userName, status);

        for(SelfTrainingList self : selfTrainingList) {
            // 부서
            if(self.getOrgTeam().isEmpty())
                self.setDepartment(self.getOrgDepart());
            else
                self.setDepartment(self.getOrgDepart() + "/" + self.getOrgTeam());

            // 교육기간
            if(self.getFromDate().equals(self.getToDate()))
                self.setPeriod(self.getFromDate());
            else
                self.setPeriod(self.getFromDate() + " ~ " + self.getToDate());

            // commit
            if(self.getIsCommit().equals("0"))
                self.setCommitStatus("진행중");
            else if(self.getIsCommit().equals("1"))
                self.setCommitStatus("완료");
        }

        //참조 파일 찾기
        InputStream is = CurriculumVitaeReportService.class.getResourceAsStream("selftTrainingList.xlsx");
        Context context = new Context(); //참조 파일에 넣을 Context 생성
        context.putVar("selflist", selfTrainingList); //Context 안에 참조 파일에서 사용할 리스트 선언

        //파일이름 지정 및 저장
        response.setHeader("Content-Disposition", "attachment; filename=\"Self-Training List(" + DateUtil.getTodayString() + ").xlsx\"");

        //JxlsHelper를 통해서 inputstream 내용에 context 반영하여 outputstream에 지정.
        JxlsHelper.getInstance().processTemplate(is, response.getOutputStream(), context);
    }
}
