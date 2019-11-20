package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.JobDescriptionVersionRepository;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        userJobDescription.setStatus(JobDescriptionStatus.ASSIGNED);
        userJobDescriptionRepository.save(userJobDescription);

        //TODO 알림 전송(JD 배정 알림)
        return "redirect:/employees/jd";
    }

    @PostMapping("employees/jd/approval")
    public String approvalEmployeeJD(@RequestParam("id") Integer id) {
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setApprovedDate(new Date());
            userJobDescription.setStatus(JobDescriptionStatus.APPROVED);
            userJobDescription.setApprovedSign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");

            userJobDescriptionRepository.save(userJobDescription);

            //TODO Job Description (승인 알림)
        }

        return "redirect:/employees/jd";
    }
}
