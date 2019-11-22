package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.*;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.JobDescriptionVersionRepository;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.common.utils.Base64Utils;
import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JobDescriptionVersionRepository jobDescriptionVersionRepository;
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private final SignatureRepository signatureRepository;
    private final JobDescriptionFileService jobDescriptionFileService;
    private final JobDescriptionReportService jobDescriptionReportService;
    private PageInfo pageInfo = new PageInfo();


    @GetMapping("/employees")
    public String getEmployees(Model model) {
        pageInfo.setParentId("m-team-dept");
        pageInfo.setParentTitle("Team/Dept");

        pageInfo.setPageId("m-emp");
        pageInfo.setPageTitle("Employees");
        model.addAttribute(pageInfo);

        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            model.addAttribute("accounts", optionalAccounts.get());
        }
        return "content/employee/list";
    }

    @GetMapping("/employees/jd")
    public String getEmployeeJD(@RequestParam(value = "id", required = false, defaultValue = "") String userId, Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Dept Employees");

        pageInfo.setPageId("m-jd");
        pageInfo.setPageTitle("Job Description");
        model.addAttribute(pageInfo);

        List<JobDescriptionVersion> jobDescriptionVersions = jobDescriptionVersionRepository.getCurrentJobDescriptionVersions();
        model.addAttribute("jdVersions", jobDescriptionVersions);

        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            model.addAttribute("users", optionalAccounts.get());
            model.addAttribute("userId", userId);
            model.addAttribute("userJobDescriptions", userRepository.getUserJobDescriptionByParentUserId(SessionUtil.getUserId()));
        }
        return "content/employee/jd/list";
    }

    @PostMapping("/employees/jd")
    public String assignEmployeeJD(UserJobDescription userJobDescription) {
        userJobDescription.setEmployeeName(userRepository.findByUserId(userJobDescription.getUsername()).getEngName());
        userJobDescription.setStatus(JobDescriptionStatus.ASSIGNED);
        userJobDescriptionRepository.save(userJobDescription);

        //TODO 알림 전송(JD 배정 알림)
        return "redirect:/employees/jd";
    }

    @PostMapping("employees/jd/approval")
    public String approvalEmployeeJD(@RequestParam("id") Integer id) {
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        if(optionalUserJobDescription.isPresent()) {
            Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setApprovedDate(new Date());
            userJobDescription.setStatus(JobDescriptionStatus.APPROVED);
            userJobDescription.setApprovedSign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
            userJobDescription.setManagerName(SessionUtil.getUserDetail().getUser().getEngName());
            userJobDescription.setManagerTitle(SessionUtil.getUserDetail().getUser().getComPosition());//TODO Job Title 확인 필요

            /**
             *  같은 JD의 이전 버전을 Superseded 상태로 변경한다.
             */
            superseded(userJobDescription.getUsername(), userJobDescription.getJobDescriptionVersion().getJobDescription().getId());

            userJobDescriptionRepository.save(userJobDescription);

            //TODO Job Description (승인 알림)
        }

        return "redirect:/employees/jd";
    }

    private void superseded(String username, Integer jobDescriptionId) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(username));
        builder.and(qUserJobDescription.jobDescriptionVersion.jobDescription.id.eq(jobDescriptionId));
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findOne(builder);
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setStatus(JobDescriptionStatus.SUPERSEDED);

            userJobDescriptionRepository.save(userJobDescription);
        }
    }

    @GetMapping("/employees/jd/{id}/print")
    public void getEmployeeJD(@PathVariable("id") Integer id, HttpServletResponse response) throws Exception {
        UserJobDescription userJobDescription = userJobDescriptionRepository.findById(id).get();
        JobDescriptionVersionFile file = userJobDescription.getJobDescriptionVersion().getJobDescriptionVersionFile();
        Resource resource = jobDescriptionFileService.loadFileAsResource(file.getSaveName());
        String format = "dd-MMM-yyyy";
        JobDescriptionSign jobDescriptionSign = JobDescriptionSign.builder()
                .assignDate(DateUtil.getDateToString(userJobDescription.getAssignDate(), format).toUpperCase())
                .agreeDate(DateUtil.getDateToString(userJobDescription.getAgreeDate(), format).toUpperCase())
                .employeeName(userJobDescription.getEmployeeName())
                .approvedDate(DateUtil.getDateToString(userJobDescription.getApprovedDate(), format).toUpperCase())
                .managerName(userJobDescription.getManagerName())
                .managerTitle(userJobDescription.getManagerTitle())
                .empSignStr(userJobDescription.getAgreeSign())
                .mngSignStr(userJobDescription.getApprovedSign())
                .empSign(StringUtils.isEmpty(userJobDescription.getAgreeSign()) ? null : new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(userJobDescription.getAgreeSign()))))
                .mngSign(StringUtils.isEmpty(userJobDescription.getApprovedSign()) ? null : new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(userJobDescription.getApprovedSign())))).build();

        response.setHeader("Content-Disposition", "attachment; filename=\""+file.getFileName().substring(0, file.getFileName().lastIndexOf(".")) + ".pdf\"");
        response.setContentType("application/pdf");
        jobDescriptionReportService.generateReport(jobDescriptionSign, resource.getInputStream(), response);
    }


}
