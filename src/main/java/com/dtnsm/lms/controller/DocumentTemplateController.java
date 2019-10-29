package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.domain.DocumentTemplate;
import com.dtnsm.lms.service.DocumentFileService;
import com.dtnsm.lms.service.DocumentService;
import com.dtnsm.lms.service.DocumentTemplateService;
import com.dtnsm.lms.service.MailService;
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

import javax.activation.MimetypesFileTypeMap;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/admin/document/template")
public class DocumentTemplateController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentTemplateController.class);

    @Autowired
    MailService mailService;

    @Autowired
    DocumentTemplateService templateService;

    @Autowired
    DocumentService documentService;

    @Autowired
    private DocumentFileService documentFileService;

    private PageInfo pageInfo = new PageInfo();

    private BorderMaster borderMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public DocumentTemplateController() {
        pageInfo.setParentId("m-document");
        pageInfo.setParentTitle("전자결재");
    }


    // 전자결재 양식 조회
    @GetMapping("/list")
    public String listPage(Model model) {

        String pageTitle = "전자결재 조회";
        pageInfo.setPageTitle(pageTitle);

        List<DocumentTemplate> documents = templateService.getList();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documents);

        return "admin/document/template/list";
    }


    // 전자결재 양식 View
    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") int id, Model model) {

        DocumentTemplate template = templateService.getById(id);

        pageInfo.setPageTitle("전자결재 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", template);

        return "admin/document/template/view";
    }

    // 전자결재 양식 등록
    @GetMapping("/add")
    public String noticeAdd(DocumentTemplate template, Model model) {

        pageInfo.setPageTitle("결재양식 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("border", template);

        return "admin/document/template/add";
    }

    // 전자결재 양식 저장
    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid DocumentTemplate template
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/document/template/add";
        }

        templateService.save(template);

        return "redirect:/admin/document/template/list";
    }

    // 전자결재 양식 수정
    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") int id, Model model) {

        DocumentTemplate template = templateService.getById(id);

        pageInfo.setPageTitle(template.getTitle() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("border", template);
        model.addAttribute("id", template.getId());

        return "admin/document/template/edit";
    }

    // 전자결재 양식 수정 저장
    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") int id
                                    , @Valid DocumentTemplate template
                                    , BindingResult result) {
        if(result.hasErrors()) {
            template.setId(id);
            return "/admin/document/template/list";
        }

        templateService.save(template);

        return "redirect:/admin/document/template/list";
    }

    // 전자결재 양식 삭제
    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") int id) {

        DocumentTemplate template = templateService.getById(id);

        templateService.delete(template);

        return "redirect:/admin/document/template/list";
    }

}
