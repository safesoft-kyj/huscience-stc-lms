package com.dtnsm.lms.controller;

import com.dtnsm.lms.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/binder")
public class DigitalBinderController {

    private PageInfo pageInfo = new PageInfo();

    public DigitalBinderController() {
        pageInfo.setParentId("m-binder");
        pageInfo.setParentTitle("Digital Binder");
    }

    @GetMapping("/cv")
    public String cv(Model model) {

        pageInfo.setPageId("m-binder-cv");
        pageInfo.setPageTitle("CV");
        model.addAttribute(pageInfo);

        return "content/binder/cv";
    }

    @GetMapping("/jd")
    public String jd(Model model) {

        pageInfo.setPageId("m-binder-jd");
        pageInfo.setPageTitle("JD");
        model.addAttribute(pageInfo);

        return "content/binder/jd";
    }

    @GetMapping("/tmlog")
    public String tmLog(Model model) {

        pageInfo.setPageId("m-binder-tmlog");
        pageInfo.setPageTitle("Employee Training Log(TM)");
        model.addAttribute(pageInfo);

        return "content/binder/tmlog";
    }

    @GetMapping("/soplog")
    public String sopLog(Model model) {

        pageInfo.setPageId("m-binder-soplog");
        pageInfo.setPageTitle("Employee Training Log(eSOP)");
        model.addAttribute(pageInfo);

        return "content/binder/soplog";
    }

    @GetMapping("/certi")
    public String certificates(Model model) {

        pageInfo.setPageId("m-binder-month");
        pageInfo.setPageTitle("Certificates");
        model.addAttribute(pageInfo);

        return "content/binder/certi";
    }
}
