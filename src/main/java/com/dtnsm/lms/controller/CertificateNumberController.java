package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.CourseCertificateNumber;
import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
import com.dtnsm.lms.service.CourseCertificateService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/certificate/number")
public class CertificateNumberController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificateNumberController.class);

    @Autowired
    CourseCertificateService courseCertificateService;


    @Autowired
    CourseCertificateNumberRepository courseCertificateNumberRepository;


    private PageInfo pageInfo = new PageInfo();

    public CertificateNumberController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정기준정보");
    }

    @GetMapping("")
    public String list(Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("수료증채번");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseCertificateNumberRepository.findAll());

        return "admin/certificate/number/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("수료증채번");

        CourseCertificateNumber courseCertificateNumber = new CourseCertificateNumber();

        model.addAttribute(pageInfo);
        model.addAttribute("border", courseCertificateNumber);

        return "admin/certificate/number/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid CourseCertificateNumber courseCertificateNumber, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/certificate/number/add";
        }

        courseCertificateNumberRepository.save(courseCertificateNumber);

        return "redirect:/admin/certificate/number";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("수료증채번");
        model.addAttribute(pageInfo);
        model.addAttribute("border", courseCertificateNumberRepository.findById(id).get());

        return "admin/certificate/number/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String editPost(@PathVariable("id") int id, @Valid CourseCertificateNumber courseCertificateNumber, BindingResult result) {
        if(result.hasErrors()) {
            courseCertificateNumber.setId(id);
            return "notice-edit";
        }
        courseCertificateNumberRepository.save(courseCertificateNumber);

        return "redirect:/admin/certificate/number";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {

        CourseCertificateNumber courseCertificateNumber = courseCertificateNumberRepository.findById(id).get();

        courseCertificateNumberRepository.delete(courseCertificateNumber);

        return "redirect:/admin/certificate/number";
    }
}