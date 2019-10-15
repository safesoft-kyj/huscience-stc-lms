package com.dtnsm.lms.mybatis.controller;

import com.dtnsm.lms.controller.BorderAdminController;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BorderAdminController.class);

    @Autowired
    UserMapperService userMapperService;

    PageInfo pageInfo = new PageInfo();

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageTitle("사원조회");

        model.addAttribute(pageInfo);
        model.addAttribute("users", userMapperService.getUserAll());

        return "admin/user/list";
    }

}
