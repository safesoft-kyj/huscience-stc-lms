package com.dtnsm.lms.Controller;

import com.dtnsm.lms.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private PageInfo pageInfo = new PageInfo();

    public MainController() {
        pageInfo.setParentId("m-basecode");
        pageInfo.setParentTitle("코드관리");
    }

    @GetMapping("/")
    public String root(Model model) {

        model.addAttribute(pageInfo);

        return "content/home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
}