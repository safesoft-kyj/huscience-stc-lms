package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.MunjeBank;
import com.dtnsm.lms.domain.constant.MunjeType;
import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.service.MunjeBankService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/munjeBank")
public class MunjeBankAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MunjeBankAdminController.class);

    @Autowired
    MunjeBankService munjeBankService;

    @Autowired
    CourseService courseService;

    @Autowired
    CodeService codeService;

    private PageInfo pageInfo = new PageInfo();

    public MunjeBankAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정유형");
    }

    @GetMapping("/list/{munjeType}")
    public String listPage(@PathVariable("munjeType") MunjeType munjeType
            , Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("시험문제조회");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", munjeBankService.getListByMunjeType(munjeType));

        return "admin/munjeBank/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        MunjeBank munjeBank = munjeBankService.getById(id);

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("교육과정유형수정");
        model.addAttribute(pageInfo);
        model.addAttribute("course", munjeBankService.save(munjeBank));

        return "admin/munjeBank/view";
    }

    @GetMapping("/add")
    public String add(Model model) {

        MunjeBank munjeBank = new MunjeBank();

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("교육과정등록");
        model.addAttribute(pageInfo);
        model.addAttribute("munjeBank", munjeBank);

        return "admin/munjeBank/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid MunjeBank munjeBank, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/munjeBank/add";
        }

        munjeBankService.save(munjeBank);

        return "redirect:/admin/munjeBank/list/" + munjeBank.getMunjeType();
    }

    @GetMapping("/addExcel")
    public String addExcel(Model model) {

        MunjeBank munjeBank = new MunjeBank();

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("교육과정등록");
        model.addAttribute(pageInfo);
        model.addAttribute("munjeBank", munjeBank);

        return "admin/munjeBank/addExcel";
    }

    @PostMapping("/addExcel-post")
    public String addExcelPost(@RequestParam("file") MultipartFile file, @Valid MunjeBank munjeBank, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/munjeBank/addExcel";
        }

        munjeBankService.save(munjeBank);

        return "redirect:/admin/munjeBank/list/" + munjeBank.getMunjeType();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("교육과정수정");
        model.addAttribute(pageInfo);
        model.addAttribute("munjeBank", munjeBankService.getById(id));

        return "admin/munjeBank/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id, @Valid MunjeBank munjeBank, BindingResult result) {
        if(result.hasErrors()) {
            munjeBank.setId(id);
            return "notice-edit";
        }
        munjeBankService.save(munjeBank);

        return "redirect:/admin/munjeBank/list/" + munjeBank.getMunjeType();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        MunjeBank munjeBank = munjeBankService.getById(id);
        MunjeType munjeType = munjeBank.getMunjeType();

        munjeBankService.delete(munjeBank);

        return "redirect:/admin/munjeBank/list/" + munjeType;
    }


}