package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.JobDescription;
import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.constant.JobDescriptionVersionStatus;
import com.dtnsm.lms.service.JobDescriptionService;
import com.dtnsm.lms.service.JobDescriptionVersionService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/jd")
@Slf4j
@SessionAttributes({"jobDescriptionVersion"})
public class JobDescriptionController {

    @Autowired
    JobDescriptionService jobDescriptionService;

    @Autowired
    JobDescriptionVersionService jobDescriptionVersionService;

    private PageInfo pageInfo = new PageInfo();
    private String pageTitle = "Job Description";

    public JobDescriptionController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle(pageTitle);
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle(pageTitle + " List");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", jobDescriptionService.getList());

        return "admin/jd/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle(pageTitle + " Insert");
        model.addAttribute(pageInfo);
        return "admin/jd/add";
    }

    @PostMapping("/add")
    @Transactional
    public String uploadJobDescription(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle(pageTitle + " Insert");
        model.addAttribute(pageInfo);

        if(!ObjectUtils.isEmpty(file) && file.isEmpty() == false) {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            List<XWPFTable> tables = document.getTables();
            if (ObjectUtils.isEmpty(tables) == false && tables.size() == 4) {

                String jobTitle = tables.get(0).getRow(0).getCell(1).getText();
//                System.out.println("Job Title : " + jobTitle);
                String fullName = jobTitle.substring(0, jobTitle.indexOf("(")).trim();
                String shortName = jobTitle.substring(jobTitle.indexOf("(") + 1, jobTitle.indexOf(")")).trim();
                log.debug("Job Title : {}, {}, {}", jobTitle, fullName, shortName);

                XWPFTable versionHistory = tables.get(3);
                int rowIndex = 0;
                String versionNo = null;
                String releaseDate = null;
                for (XWPFTableRow row : versionHistory.getRows()) {
                    if (row.getTableCells().size() == 2) {
                        if (rowIndex > 1) {
                            versionNo = row.getTableCells().get(0).getText().split(" ")[1];
                            releaseDate = row.getTableCells().get(1).getText();

                            log.debug("versionNo : {}, releaseDate : {}", releaseDate, versionNo);
                        }
                    }
                    rowIndex++;
                }

                if (!StringUtils.isEmpty(versionNo) && !StringUtils.isEmpty(releaseDate)) {
                    Optional<JobDescription> optionalJobDescription = jobDescriptionService.findByShortName(shortName);
                    JobDescription jobDescription = optionalJobDescription.isPresent() ? optionalJobDescription.get() : new JobDescription();
                    jobDescription.setShortName(shortName);
                    jobDescription.setTitle(fullName);

                    /**
                     * 이전 버전이 존재 하는 경우 SUPERSEDED 상태로 변경 한다.
                     */
                    if(!ObjectUtils.isEmpty(jobDescription.getId())) {
                        Optional<JobDescriptionVersion> optionalJobDescriptionVersion = jobDescriptionVersionService.findByJobDescriptionId(jobDescription.getId());
                        if(optionalJobDescriptionVersion.isPresent()) {
                            JobDescriptionVersion currentVersion = optionalJobDescriptionVersion.get();
                            currentVersion.setStatus(JobDescriptionVersionStatus.SUPERSEDED);

                            jobDescriptionVersionService.update(currentVersion);
                        }
                    }

                    JobDescriptionVersion jobDescriptionVersion = new JobDescriptionVersion();
                    jobDescriptionVersion.setJobDescription(jobDescription);
                    jobDescriptionVersion.setVersion_no(versionNo);
                    jobDescriptionVersion.setRelease_date(DateUtil.getStringToDate(releaseDate, "dd-MMM-yyyy"));
                    jobDescriptionVersion.setFile(file);

                    if(ObjectUtils.isEmpty(jobDescription.getId()) == false) {
                        Optional<JobDescriptionVersion> optionalJobDescriptionVersion = jobDescriptionVersionService.findByJobDescriptionVersion(jobDescriptionVersion);
                        if (optionalJobDescriptionVersion.isPresent()) {
                            return "redirect:/admin/jd/list";
                        }
                    }

                    model.addAttribute("jobDescriptionVersion", jobDescriptionVersion);
                    return "admin/jd/add-info";
                } else {
                    return "redirect:/admin/jd/add";
                }
            } else {
                return "redirect:/admin/jd/add";
            }
        } else {
            return "redirect:/admin/jd/add";
        }
    }

    @PostMapping("/add-post")
    public String addPost(@ModelAttribute("jobDescriptionVersion") JobDescriptionVersion jobDescriptionVersion, SessionStatus status) {
        log.debug("@JobDescriptionVersion : {}", jobDescriptionVersion);

        jobDescriptionVersionService.save(jobDescriptionVersion);
        status.setComplete();
        return "redirect:/admin/jd/list";
    }


//    @GetMapping("/edit/{id}")
//    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
//        JobDescription obj = jobDescriptionService.getById(id);
//
//        pageInfo.setPageId("m-customer-edit");
//        pageInfo.setPageTitle(pageTitle + " Edit");
//        model.addAttribute(pageInfo);
//        model.addAttribute("jobDescription", obj);
//
//        return "admin/jd/edit";
//    }
//
//    @PostMapping("/edit-post/{id}")
//    public String updateCustomer(@PathVariable("id") Integer id, @Valid JobDescription jobDescription, BindingResult result) {
//        if(result.hasErrors()) {
//            jobDescription.setId(id);
//            return "admin/jd/edit";
//        }
//
//        jobDescriptionService.save(jobDescription);
//
//        return "redirect:/admin/jd/list";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteCustomer(@PathVariable("id") Integer id) {
//
//        JobDescription obj = jobDescriptionService.getById(id);
//
//        jobDescriptionService.delete(obj);
//
//        return "redirect:/admin/jd/list";
//    }
}
