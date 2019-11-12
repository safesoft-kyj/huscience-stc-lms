package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.BinderJd;
import com.dtnsm.lms.domain.JobDescriptionVersion;
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
@RequestMapping("/admin/binder/jd")
public class BinderJdController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BinderJdController.class);

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

    public BinderJdController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("Digital Binder");
//        pageInfo.setParentTitle("Curriculum Vitae");
    }

    @GetMapping("/list")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Search");

        List<BinderJd> obj = binderJdService.getListByIsActiveJd();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", obj);

        return "admin/binder/jd/list";
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("border", binderJdService.getById(id));

        return "admin/binder/jd/view";
    }

    @GetMapping("/jdview/{id}")
    public String jdViewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("border", binderJdService.getById(id));

        return "admin/binder/jd/jdview";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Add");

        model.addAttribute(pageInfo);
        model.addAttribute("binderJd", new BinderJd());
        model.addAttribute("jdList", jobDescriptionVersionService.getListByAcitveJd("1"));
        model.addAttribute("userList", userService.getAccountList());

        return "admin/binder/jd/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid BinderJd obj
            , @RequestParam(value = "userList", required = false, defaultValue = "0") String[] userList
            , BindingResult result) {

        if(result.hasErrors()) {
            return "admin/Binder/jd/add";
        }


        for(String userId : userList) {

            BinderJd activeBinderJd = binderJdService.getByUserActiveVersionJd(userId, obj.getJdVersion().getId());

            if(activeBinderJd != null) {
                activeBinderJd.setIsActive("0");
            }

            obj.setRegDate(DateUtil.getTodayString());
            obj.setAccount(userService.getAccountByUserId(userId));
            obj.setIsActive("1");
            BinderJd binderJd = binderJdService.save(obj);
        }


//
//        if (activeBinderJd == null) {
//            obj.setRegDate(DateUtil.getTodayString());
//            obj.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
//            obj.setIsActive("1");
//
//            BinderJd binderJd = binderJdService.save(obj);
//        }


        return "redirect:/admin/binder/jd/list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        BinderJd obj = binderJdService.getById(id);

        BinderJd newBinderJd = new BinderJd();
        newBinderJd.setAccount(obj.getAccount());
        newBinderJd.setJdVersion(obj.getJdVersion());

        pageInfo.setPageTitle(pageTitleHead + "Edit");
        model.addAttribute(pageInfo);
        model.addAttribute("jdList", jobDescriptionVersionService.getListByAcitveJd("1"));
        model.addAttribute("binderJd", newBinderJd);

        return "admin/binder/jd/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String updateCustomer(@PathVariable("id") long id
            , @Valid BinderJd obj
            , BindingResult result) {
        if(result.hasErrors()) {
            obj.setId(id);
            return "admin/binder/jd/edit";
        }

        BinderJd oldBinderJd = binderJdService.getUserActiveJd(SessionUtil.getUserId());

        if (oldBinderJd != null) {

            oldBinderJd.setIsActive("0");
            oldBinderJd = binderJdService.save(oldBinderJd);

            obj.setRegDate(DateUtil.getTodayString());
            obj.setIsActive("1");
            obj.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));

            BinderJd binderJd = binderJdService.save(obj);

        }

        return "redirect:/admin/binder/jd/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        BinderJd obj = binderJdService.getById(id);

        binderJdService.delete(obj);

        return "redirect:/admin/binder/jd/list";
    }
}