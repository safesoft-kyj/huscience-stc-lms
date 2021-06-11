package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.CourseManager;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateLogRepository;
import com.dtnsm.lms.service.CourseCertificateService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/certificate/info")
public class CertificateInfoController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificateInfoController.class);

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
        pageInfo.setParentTitle("교육과정기준정보");
    }

    @GetMapping("")
    public String list(Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("수료증정보");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseCertificateInfoRepository.findAll());

        return "admin/certificate/info/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("수료증정보");

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

        return "redirect:/admin/certificate/info";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("수료증정보");
        model.addAttribute(pageInfo);
        model.addAttribute("certiInfo", courseCertificateInfoRepository.findById(id).get());
        model.addAttribute("managerList", userService.getAccountList());

        return "admin/certificate/info/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String editPost(@PathVariable("id") int id, @Valid CourseCertificateInfo courseCertificateInfo, BindingResult result) {
        if(result.hasErrors()) {
            courseCertificateInfo.setId(id);
            return "admin/certificate/info/edit";
        }

        CourseCertificateInfo oldCourseCertificateInfo = courseCertificateInfoRepository.findById(id).get();
        courseCertificateInfo.setIsActive(oldCourseCertificateInfo.getIsActive());

        CourseCertificateInfo newCourseCertificateInfo1 = courseCertificateInfoRepository.save(courseCertificateInfo);

        return "redirect:/admin/certificate/info";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes attributes) {

        List<CourseCertificateInfo> certificateInfoList = courseCertificateInfoRepository.findAll();
        if(certificateInfoList.size() <= 1) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "수료증정보는 반드시 설정되어야 합니다.");
        } else {
            CourseCertificateInfo courseCertificateInfo = courseCertificateInfoRepository.findById(id).get();
            if(courseCertificateInfo.getIsActive() == 1) {
                attributes.addFlashAttribute("type", "warning-top");
                attributes.addFlashAttribute("msg", "선택한 수료증정보는 Active 상태임으로 삭제할 수 없습니다.");
            } else  {
                courseCertificateInfoRepository.delete(courseCertificateInfo);
            }
        }
        return "redirect:/admin/certificate/info";
    }

    @GetMapping("/updateActive/{id}")
    public String updateActive(@PathVariable("id") int id) {

        // 모든 설문을 초기화 한다.
        for(CourseCertificateInfo courseCertificateInfo : courseCertificateInfoRepository.findAll()) {
            courseCertificateInfo.setIsActive(0);
            courseCertificateInfoRepository.save(courseCertificateInfo);
        }

        // 기본을 변경
        CourseCertificateInfo courseCertificateInfo = courseCertificateInfoRepository.findById(id).get();
        courseCertificateInfo.setIsActive(1);
        courseCertificateInfoRepository.save(courseCertificateInfo);

        return "redirect:/admin/certificate/info";
    }
}