package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.JobDescriptionVersion;
import com.dtnsm.lms.domain.JobDescriptionVersionFile;
import com.dtnsm.lms.service.BinderJdService;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.service.JobDescriptionService;
import com.dtnsm.lms.service.JobDescriptionVersionService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/jd/version")
public class JobDescriptionVersionController {


    @Autowired
    JobDescriptionVersionService versionService;

    @Autowired
    JobDescriptionService jobDescriptionService;

    @Autowired
    BinderJdService binderJdService;

    @Autowired
    JobDescriptionFileService fileService;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();
    private String pageTitle = "Job Description";

    public JobDescriptionVersionController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle(pageTitle);
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle(pageTitle + " List");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", versionService.getListByAcitveJd("1"));

        return "admin/jd/version/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle(pageTitle + " Insert");
        model.addAttribute(pageInfo);
        model.addAttribute("jdVersion", new JobDescriptionVersion());
        model.addAttribute("jdList", jobDescriptionService.getList());

        return "admin/jd/version/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid JobDescriptionVersion jdVersion
            , @RequestParam("files") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/jd/version/add";
        }

        JobDescriptionVersion activeJd = versionService.getByJdIdAndActiveJd(jdVersion.getJd().getId());

        if (activeJd == null) {
            jdVersion.setRegDate(DateUtil.getTodayString());
            jdVersion.setVer(1.0);
            jdVersion.setIsActive("1");

            JobDescriptionVersion version = versionService.save(jdVersion);
            fileService.storeFile(file, version);
        }

        return "redirect:/admin/jd/version/list";
    }


    // Job Description Version 이력 조회
    @GetMapping("/viewHistory/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        JobDescriptionVersion version = versionService.getById(id);

        List<JobDescriptionVersion> jobDescriptionVersions = versionService.getListByJdIdOrderByVerDesc(version.getJd().getId());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", jobDescriptionVersions);

        return "/admin/jd/version/viewHistory";
    }

    // Job Description 사용자 조회
    @GetMapping("/viewUser/{id}")
    public String userViewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        JobDescriptionVersion version = versionService.getById(id);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", version.getBinderJds());

        return "/admin/jd/version/viewUser";
    }

    // Job Description File 조회
    @GetMapping("/viewJdFile/{id}")
    public String jdViewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("jdVersion", versionService.getById(id));

        return "/admin/jd/version/viewJdFile";
    }


    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        JobDescriptionVersion obj = versionService.getById(id);

        JobDescriptionVersion newVersion = new JobDescriptionVersion();
        newVersion.setJd(obj.getJd());

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle(pageTitle + " Edit");
        model.addAttribute(pageInfo);
        model.addAttribute("jdVersion", newVersion);
        model.addAttribute("jdList", jobDescriptionService.getList());

        return "admin/jd/version/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String updateCustomer(@PathVariable("id") long id
            , @Valid JobDescriptionVersion jdVersion
            , @RequestParam("files") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            jdVersion.setId(id);
            return "admin/jd/version/edit";
        }


        JobDescriptionVersion activeJd = versionService.getByJdIdAndActiveJd(jdVersion.getJd().getId());

        if (activeJd != null) {

            activeJd.setIsActive("0");
            activeJd = versionService.save(activeJd);

            jdVersion.setRegDate(DateUtil.getTodayString());
            jdVersion.setVer(activeJd.getVer() + 0.1);
            jdVersion.setIsActive("1");

            JobDescriptionVersion version = versionService.save(jdVersion);

            fileService.storeFile(file, version);
        }

        return "redirect:/admin/jd/version/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        JobDescriptionVersion obj = versionService.getById(id);

        versionService.delete(obj);

        return "redirect:/admin/jd/version/list";
    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request){

        JobDescriptionVersionFile documentFile = fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(documentFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(documentFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, documentFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}
