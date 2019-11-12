package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.JobDescription;
import com.dtnsm.lms.service.JobDescriptionService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/jd")
public class JobDescriptionController {

    @Autowired
    JobDescriptionService jobDescriptionService;

    private PageInfo pageInfo = new PageInfo();
    private String pageTitle = "Job Description";

    public JobDescriptionController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle(pageTitle);
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle(pageTitle + " List");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", jobDescriptionService.getList());

        return "admin/jd/list";
    }

    @GetMapping("/add")
    public String add(JobDescription jobDescription, Model model) {

        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle(pageTitle + " Insert");
        model.addAttribute(pageInfo);

        return "admin/jd/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid JobDescription jobDescription
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/jd/add";
        }

        jobDescription.setRegDate(DateUtil.getTodayString());
        jobDescriptionService.save(jobDescription);


        return "redirect:/admin/jd/list";
    }


    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        JobDescription obj = jobDescriptionService.getById(id);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle(pageTitle + " Edit");
        model.addAttribute(pageInfo);
        model.addAttribute("jobDescription", obj);

        return "admin/jd/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String updateCustomer(@PathVariable("id") long id, @Valid JobDescription jobDescription, BindingResult result) {
        if(result.hasErrors()) {
            jobDescription.setId(id);
            return "admin/jd/edit";
        }

        jobDescription.setRegDate(DateUtil.getTodayString());
        jobDescriptionService.save(jobDescription);

        return "redirect:/admin/jd/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        JobDescription obj = jobDescriptionService.getById(id);

        jobDescriptionService.delete(obj);

        return "redirect:/admin/jd/list";
    }
}
