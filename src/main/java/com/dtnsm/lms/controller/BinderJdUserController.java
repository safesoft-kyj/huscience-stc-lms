package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.BinderJd;
import com.dtnsm.lms.service.BinderJdService;
import com.dtnsm.lms.service.JobDescriptionService;
import com.dtnsm.lms.service.JobDescriptionVersionService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/binder/jd")
public class BinderJdUserController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BinderJdUserController.class);

    private String pageTitleHead = "Digital Binder Job Description ";

    @Autowired
    BinderJdService binderJdService;

    @Autowired
    JobDescriptionService jobDescriptionService;

    @Autowired
    JobDescriptionVersionService jobDescriptionVersionService;

    @Autowired
    UserServiceImpl userService;

    private PageInfo pageInfo = new PageInfo();

    public BinderJdUserController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("Digital Binder");
//        pageInfo.setParentTitle("Curriculum Vitae");
    }

    @GetMapping("/list")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Search");

        List<BinderJd> obj = binderJdService.getUserJdListOrderByVerDesc(SessionUtil.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", obj);

        return "content/binder/jd/list";
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("border", binderJdService.getById(id));

        return "content/binder/jd/view";
    }


}