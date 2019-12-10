package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.service.BorderMasterService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/border-master")
public class BorderMasterController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BorderMasterController.class);

    @Autowired
    BorderMasterService borderMasterService;

    private PageInfo pageInfo = new PageInfo();

    public BorderMasterController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("기준정보관리");

    }

    @GetMapping("/list")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        Page<BorderMaster> borders = borderMasterService.getPageList(pageable);

        pageInfo.setPageId("m-border-list-page");
        pageInfo.setPageTitle("게시판");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);

        return "admin/border-master/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") String id, Model model) {

        BorderMaster oldBorder = borderMasterService.getById(id);

        pageInfo.setPageId("m-border-edit");
        pageInfo.setPageTitle("게시판");
        model.addAttribute(pageInfo);
        model.addAttribute("border", borderMasterService.save(oldBorder));

        return "admin/border-master/view";
    }

    @GetMapping("/add")
    public String add(Model model) {

        BorderMaster borderMaster = new BorderMaster();
        borderMaster.setId(borderMasterService.getBorderNumber());

        pageInfo.setPageId("m-border-add");
        pageInfo.setPageTitle("게시판");
        model.addAttribute(pageInfo);
        model.addAttribute("border", borderMaster);
        model.addAttribute("minorList", borderMasterService.getTypeList());

        return "admin/border-master/add";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid BorderMaster borderMaster, BindingResult result) {
        if(result.hasErrors()) {
            return "notice-add";
        }

        borderMasterService.save(borderMaster);

        return "redirect:/admin/border-master/list";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") String id, Model model) {

        pageInfo.setPageId("m-border-edit");
        pageInfo.setPageTitle("게시판");
        model.addAttribute(pageInfo);
        model.addAttribute("border", borderMasterService.getById(id));
        model.addAttribute("minorList", borderMasterService.getTypeList());

        return "admin/border-master/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") String id, @Valid BorderMaster elBorder, BindingResult result) {
        if(result.hasErrors()) {
            elBorder.setId(id);
            return "notice-edit";
        }
        borderMasterService.save(elBorder);

        return "redirect:/admin/border-master/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") String id) {

        BorderMaster border = borderMasterService.getById(id);

        borderMasterService.delete(border);

        return "redirect:/admin/border-master/list";
    }
}