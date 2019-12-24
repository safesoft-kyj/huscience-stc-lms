package com.dtnsm.lms.controller;

import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.domain.ElMajor;
import com.dtnsm.lms.domain.ElMinor;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/code")
public class BaseCodeController {

    @Autowired
    CodeService codeService;

    private PageInfo pageInfo = new PageInfo();

    public BaseCodeController() {
        pageInfo.setParentId("m-admincode");
        pageInfo.setParentTitle("기준정보관리");
    }


    @GetMapping("/major")
    public String majorList(Model model) {

        pageInfo.setPageId("m-admincode-major-list");
        pageInfo.setPageTitle("Major Code");
        model.addAttribute(pageInfo);
        model.addAttribute("elMajors", codeService.getMajorList());

        return "admin/code/major-list";
    }

    @GetMapping("/major/add")
    public String majorAdd(ElMajor elbMajor, Model model) {

        pageInfo.setPageId("m-admincode-major-add");
        pageInfo.setPageTitle("Major Code");
        model.addAttribute(pageInfo);
        model.addAttribute("codeType", codeService.getManualCodeType());

        return "admin/code/major-add";
    }

    @PostMapping("/major/add-post")
    public String majorAddPost(@Valid ElMajor elbMajor, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/code/major-add";
        }

        codeService.majorSave(elbMajor);

        return "redirect:/admin/code/major";
    }


    @GetMapping("/major/edit/{id}")
    public String majorEdit(@PathVariable("id") String id, Model model) {
        ElMajor elMajor = codeService.getMajorById(id);

        pageInfo.setPageId("m-admincode-major-edit");
        pageInfo.setPageTitle("Major Code");
        model.addAttribute(pageInfo);
        model.addAttribute(elMajor);
        model.addAttribute("codeType", codeService.getManualCodeType());

        return "admin/code/major-update";
    }

    @PostMapping("/major/update-post/{id}")
    public String majorEditPost(@PathVariable("id") String id, @Valid ElMajor elMajor, BindingResult result) {
        if(result.hasErrors()) {
            elMajor.setMajorCd(id);
            return "admin/code/major-update";
        }

        codeService.majorSave(elMajor);

        return "redirect:/admin/code/major";
    }

    @GetMapping("/major-delete/{id}")
    public String majorDelete(@PathVariable("id") String id) {

        ElMajor elMajor = codeService.getMajorById(id);

        codeService.majorDelete(elMajor);

        return "redirect:/admin/code/major";
    }


    /*


     */

    @GetMapping("/minor")
    public String minorList(Model model) {

        pageInfo.setPageId("m-admincode-minor-list");
        pageInfo.setPageTitle("Minor Code");
        model.addAttribute(pageInfo);

        List<ElMinor> elMinors = codeService.getMinorList();
        model.addAttribute("elMinors", elMinors);

        return "admin/code/minor-list";
    }

    @GetMapping("/minor/add")
    public String minorAdd(ElMinor elMinor, Model model) {

        pageInfo.setPageId("m-admincode-minor-add");
        pageInfo.setPageTitle("Minor Code");
        model.addAttribute(pageInfo);
        model.addAttribute("majorList", codeService.getMajorList());
        //model.addAttribute("codeType", elCodeService.getManualCodeType());

        return "admin/code/minor-add";
    }

    @PostMapping("/minor/add-post")
    public String minorAddPost(@Valid ElMinor elMinor, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/code/minor-add";
        }

        String majorCd = elMinor.getElMajor().getMajorCd();

        elMinor.setElMajor(codeService.getMajorById(majorCd));

        codeService.minorSave(elMinor);

        return "redirect:/admin/code/minor";
    }

    @GetMapping("/minor/edit/{id}")
    public String minorEditForm(@PathVariable("id") String minorCd, Model model) {

        ElMinor elMinor = codeService.getMinorById(minorCd);

        pageInfo.setPageId("m-admincode-major-edit");
        pageInfo.setPageTitle("Minor Code");
        model.addAttribute(pageInfo);
        model.addAttribute(elMinor);
        model.addAttribute("majorList", codeService.getMajorList());

        return "admin/code/minor-update";
    }

    @PostMapping("/minor/update-post/{id}")
    public String minorEditPost(@PathVariable("id") String minorCd, @Valid ElMinor elMinor, BindingResult result) {
        if(result.hasErrors()) {
            elMinor.setMinorCd (minorCd);
            return "admin/code/minor-update";
        }

        codeService.minorSave(elMinor);

        return "redirect:/admin/code/minor";
    }

    @GetMapping("/minor/delete")
    public String minorDelete(@RequestParam("id") String minorCd) {

        codeService.minorDelete(minorCd);

        return "redirect:/admin/code/minor";
    }
}
