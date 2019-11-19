package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.mail.Session;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final UserService userService;
    private final UserRepository userRepository;
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
    public String getEmployeeJD(Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Dept Employees");

        pageInfo.setPageId("m-jd");
        pageInfo.setPageTitle("Job Description");
        model.addAttribute(pageInfo);

        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            model.addAttribute("userJobDescriptions", userRepository.getUserJobDescriptionByParentUserId(SessionUtil.getUserId()));
        }
        return "content/employee/jd/list";
    }
}
