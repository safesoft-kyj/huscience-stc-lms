package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.CourseCertificateNumber;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
import com.dtnsm.lms.service.CourseCertificateService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificateNumberController.class);

    @Autowired
    CourseCertificateService courseCertificateService;


    @Autowired
    CourseCertificateNumberRepository courseCertificateNumberRepository;


    private PageInfo pageInfo = new PageInfo();

    public CertificateNumberController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("수료증정보관리");
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("수료증정보관리");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseCertificateNumberRepository.findAll());

        return "admin/certificate/number/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("수료증정보 등록");

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

        return "redirect:/admin/certificate/number/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("수료증정보 수정");
        model.addAttribute(pageInfo);
        model.addAttribute("border", courseCertificateNumberRepository.getOne(id));

        return "admin/certificate/number/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String editPost(@PathVariable("id") int id, @Valid CourseCertificateNumber courseCertificateNumber, BindingResult result) {
        if(result.hasErrors()) {
            courseCertificateNumber.setId(id);
            return "notice-edit";
        }
        courseCertificateNumberRepository.save(courseCertificateNumber);

        return "redirect:/admin/certificate/number/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {

        CourseCertificateNumber courseCertificateNumber = courseCertificateNumberRepository.getOne(id);

        courseCertificateNumberRepository.delete(courseCertificateNumber);

        return "redirect:/admin/certificate/number/list";
    }
}