package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.JobDescriptionVersionFile;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.service.JobDescriptionService;
import com.dtnsm.lms.service.JobDescriptionVersionService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/admin/jd/v")
@Slf4j
public class JobDescriptionVersionController {


    @Autowired
    JobDescriptionVersionService versionService;

    @Autowired
    JobDescriptionService jobDescriptionService;

    @Autowired
    JobDescriptionFileService fileService;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();
    private String pageTitle = "Job Description";

    public JobDescriptionVersionController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle(pageTitle);
    }

    @GetMapping("/{id}")
    public String list(@PathVariable("id") Integer id, Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle(pageTitle + " List");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", versionService.findAllVersions(id));

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

    // Job Description Version ?????? ??????
    @GetMapping("/viewHistory/{id}")
    public String viewPage(@PathVariable("id") Integer id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        JobDescriptionVersion version = versionService.getById(id);

        List<JobDescriptionVersion> jobDescriptionVersions = versionService.getListByJdIdOrderByVerDesc(version.getJobDescription().getId());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", jobDescriptionVersions);

        return "/admin/jd/version/viewHistory";
    }

    // Job Description ????????? ??????
    @GetMapping("/viewUser/{id}")
    public String userViewPage(@PathVariable("id") Integer id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        JobDescriptionVersion version = versionService.getById(id);

        model.addAttribute(pageInfo);
//        model.addAttribute("borders", version.getBinderJds());

        return "/admin/jd/version/viewUser";
    }

    // Job Description File ??????
    @GetMapping("/viewJdFile/{id}")
    public String jdViewPage(@PathVariable("id") Integer id, Model model) {

        pageInfo.setPageTitle(pageTitle + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("jdVersion", versionService.getById(id));

        return "/admin/jd/version/viewJdFile";
    }


    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        JobDescriptionVersion obj = versionService.getById(id);

        JobDescriptionVersion newVersion = new JobDescriptionVersion();
        newVersion.setJobDescription(obj.getJobDescription());

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle(pageTitle + " Edit");
        model.addAttribute(pageInfo);
        model.addAttribute("jdVersion", newVersion);
        model.addAttribute("jdList", jobDescriptionService.getList());

        return "admin/jd/version/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String updateCustomer(@PathVariable("id") Integer id
            , @Valid JobDescriptionVersion jdVersion
            , @RequestParam("files") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            jdVersion.setId(id);
            return "admin/jd/version/edit";
        }


        JobDescriptionVersion activeJd = versionService.getByJdIdAndActiveJd(jdVersion.getJobDescription().getId());

        if (activeJd != null) {

//            activeJd.setIsActive("0");
//            activeJd = versionService.save(activeJd);
//
//            jdVersion.setRegDate(DateUtil.getTodayString());
//            jdVersion.setVer(activeJd.getVer() + 0.1);
//            jdVersion.setIsActive("1");

            JobDescriptionVersion version = versionService.save(jdVersion);

            fileService.storeFile(file, version);
        }

        return "redirect:/admin/jd/version/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id) {

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

        // ??????????????? ?????? ?????? ??????
        String newFileName = FileUtil.getNewFileName(request, documentFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}
