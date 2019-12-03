package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.CourseCertificateNumber;
import com.dtnsm.lms.domain.Survey;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateLogRepository;
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
@RequestMapping("/admin/certificate/info")
public class CertificateInfoController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificateInfoController.class);

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    CourseCertificateLogRepository courseCertificateLogRepository;

    @Autowired
    CourseCertificateInfoRepository courseCertificateInfoRepository;

    @Autowired
    UserServiceImpl userService;

    private PageInfo pageInfo = new PageInfo();

    public CertificateInfoController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("수료증관리");
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("수료증정보조회");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseCertificateInfoRepository.findAll());

        return "admin/certificate/info/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("수료증정보 등록");

        CourseCertificateInfo courseCertificateInfo = new CourseCertificateInfo();

        model.addAttribute(pageInfo);
        model.addAttribute("border", courseCertificateInfo);
        model.addAttribute("managerList", userService.getAccountList());

        return "admin/certificate/info/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid CourseCertificateInfo courseCertificateInfo, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/certificate/number/add";
        }

        courseCertificateInfoRepository.save(courseCertificateInfo);

        return "redirect:/admin/certificate/info/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("수료증정보 수정");
        model.addAttribute(pageInfo);
        model.addAttribute("border", courseCertificateInfoRepository.getOne(id));
        model.addAttribute("managerList", userService.getAccountList());

        return "admin/certificate/info/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String editPost(@PathVariable("id") int id, @Valid CourseCertificateInfo courseCertificateInfo, BindingResult result) {
        if(result.hasErrors()) {
            courseCertificateInfo.setId(id);
            return "notice-edit";
        }
        courseCertificateInfoRepository.save(courseCertificateInfo);

        return "redirect:/admin/certificate/info/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {

        CourseCertificateInfo courseCertificateInfo = courseCertificateInfoRepository.getOne(id);

        courseCertificateInfoRepository.delete(courseCertificateInfo);

        return "redirect:/admin/certificate/info/list";
    }

    @GetMapping("/updateActive/{id}")
    public String updateActive(@PathVariable("id") int id) {

        // 모든 설문을 초기화 한다.
        for(CourseCertificateInfo courseCertificateInfo : courseCertificateInfoRepository.findAll()) {
            courseCertificateInfo.setIsActive(0);
            courseCertificateInfoRepository.save(courseCertificateInfo);
        }

        // 요청된 설문을 기본 설문으로 변경한다.
        CourseCertificateInfo courseCertificateInfo = courseCertificateInfoRepository.getOne(id);
        courseCertificateInfo.setIsActive(1);
        courseCertificateInfoRepository.save(courseCertificateInfo);

        return "redirect:/admin/certificate/info/list/";
    }
}