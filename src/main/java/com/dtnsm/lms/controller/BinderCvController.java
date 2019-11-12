package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
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
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/binder/cv")
public class BinderCvController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BinderCvController.class);

    private String pageTitleHead = "Digital Binder Curriculum Vitae ";

    @Autowired
    BinderCvService binderCvService;

    @Autowired
    BinderCvEducationService binderCvEducationService;

    @Autowired
    BinderCvProfessionalAffiliationsService binderCvProfessionalAffiliationsService;

    @Autowired
    BinderCvSkillsService binderCvSkillsService;

    @Autowired
    BinderCvLicensesCertificationService binderCvLicensesCertificationService;

    @Autowired
    BinderCvExperienceService binderCvExperienceService;

    @Autowired
    BinderCvCareerHistoryService binderCvCareerHistoryService;

    @Autowired
    UserServiceImpl userService;

    private PageInfo pageInfo = new PageInfo();

    public BinderCvController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("Digital Binder");
//        pageInfo.setParentTitle("Curriculum Vitae");
    }

    @GetMapping("/list")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Search");

        List<BinderCv> obj = binderCvService.getUserCvListOrderByVerDesc(SessionUtil.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", obj);

        return "content/binder/cv/list";
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        BinderCv obj= binderCvService.getById(id);

        pageInfo.setPageTitle(pageTitleHead + "Detail");

        model.addAttribute(pageInfo);
        model.addAttribute("account", userService.getAccountByUserId(SessionUtil.getUserId()));
        model.addAttribute("border", obj);

        return "content/binder/cv/view";
    }

    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageTitle(pageTitleHead + "Add");
        model.addAttribute(pageInfo);

        BinderCvExperience[] experienceList = {new BinderCvExperience(), new BinderCvExperience()};


        model.addAttribute("education", new BinderCvEducation());
        model.addAttribute("careerHistory", new BinderCvCareerHistory());
//        model.addAttribute("careerHistoryList", careerHistoryList);
        model.addAttribute("license", new BinderCvLicensesCertification());
        model.addAttribute("professional", new BinderCvProfessionalAffiliations());
        model.addAttribute("skills", new BinderCvSkills());
//        model.addAttribute("experience", new BinderCvExperience());
        model.addAttribute("experience", experienceList);

        return "content/binder/cv/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid BinderCvEducation binderCvEducation
                            , BinderCvLicensesCertification binderCvLicensesCertification
                            , BinderCvProfessionalAffiliations binderCvProfessionalAffiliations
                            , BinderCvSkills binderCvSkills
                            , @RequestParam("career_company") String career_company[]
                            , @RequestParam("career_company_address") String career_company_address[]
                            , @RequestParam("career_period") String career_period[]
                            , @RequestParam("career_position") String career_position[]
                            , @RequestParam("career_team") String career_team[]
                            , @RequestParam("experience_name") String experience_name[]
                            , @RequestParam("experience_content") String experience_content[]
                            , BindingResult result) {

        if(result.hasErrors()) {
            return "binder/add";
        }

        BinderCv activeBinderCv = binderCvService.getUserActiveCv(SessionUtil.getUserId());
        if (activeBinderCv == null) {
            BinderCv binderCv = new BinderCv();
            binderCv.setRegDate(DateUtil.getTodayString());
            binderCv.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
            binderCv.setIsActive("1");
            binderCv.setVer(1.0);

            BinderCv binderCv1 = binderCvService.save(binderCv);

            binderCvEducation.setBinderCv(binderCv1);
            binderCvEducationService.save(binderCvEducation);

            for(int i=0; i<career_company.length; i++) {
                if (!career_company[i].isEmpty()) {
                    BinderCvCareerHistory obj = new BinderCvCareerHistory();
                    obj.setCompany(career_company[i]);
                    obj.setCompanyAddress(career_company_address[i]);
                    obj.setPeriod(career_period[i]);
                    obj.setPosition(career_position[i]);
                    obj.setTeamDepartment(career_team[i]);
                    obj.setBinderCv(binderCv1);
                    binderCvCareerHistoryService.save(obj);
                }
            }

            binderCvLicensesCertification.setBinderCv(binderCv1);
            binderCvLicensesCertificationService.save(binderCvLicensesCertification);

            binderCvProfessionalAffiliations.setBinderCv(binderCv1);
            binderCvProfessionalAffiliationsService.save(binderCvProfessionalAffiliations);

            binderCvSkills.setBinderCv(binderCv1);
            binderCvSkillsService.save(binderCvSkills);


            for(int i=0; i<experience_name.length; i++) {
                if(!experience_name[i].isEmpty()) {
                    BinderCvExperience obj = new BinderCvExperience();
                    obj.setName(experience_name[i]);
                    obj.setContent(experience_content[i]);
                    obj.setBinderCv(binderCv1);
                    binderCvExperienceService.save(obj);
                }
            }
        }

        return "redirect:/binder/cv/list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        BinderCv obj = binderCvService.getById(id);

        pageInfo.setPageTitle(pageTitleHead + "Edit");
        model.addAttribute(pageInfo);

        // Educations
        for(BinderCvEducation tmpObj : obj.getBinderCvEducations()) {
            BinderCvEducation education = new BinderCvEducation();
            education.setEduPeriod(tmpObj.getEduPeriod());
            education.setInstitute(tmpObj.getInstitute());
            education.setInstituteAddress(tmpObj.getInstituteAddress());
            education.setDegree(tmpObj.getDegree());
            model.addAttribute("education", education);
        }

        // CareerHistories
        model.addAttribute("careerHistoryList", obj.getBinderCvCareerHistories());

        //Licenses & Certifications
        for(BinderCvLicensesCertification tmpObj : obj.getBinderCvLicensesCertifications()) {
            BinderCvLicensesCertification license = new BinderCvLicensesCertification();
            license.setLicenses(tmpObj.getLicenses());
            license.setCertifications(tmpObj.getCertifications());
            model.addAttribute("license", license);
        }

        //Professional Affiliations
        for(BinderCvProfessionalAffiliations tmpObj : obj.getBinderCvProfessionalAffiliations()) {
            BinderCvProfessionalAffiliations professional = new BinderCvProfessionalAffiliations();
            professional.setMembership(tmpObj.getMembership());
            model.addAttribute("professional", professional);
        }

        //Skills
        for(BinderCvSkills tmpObj : obj.getBinderCvSkills()) {
            BinderCvSkills skills = new BinderCvSkills();
            skills.setLanguages(tmpObj.getLanguages());
            skills.setLanguagesLevel(tmpObj.getLanguagesLevel());
            skills.setComputerKnowledge(tmpObj.getComputerKnowledge());
            skills.setComputerKnowledgeLevel(tmpObj.getComputerKnowledgeLevel());
            model.addAttribute("skills", skills);
        }

        //Experiences
        model.addAttribute("experienceList",  obj.getBinderCvExperiences());

        return "content/binder/cv/edit";
    }

    @PostMapping("/edit-post")
    public String editPost(@Valid BinderCvEducation binderCvEducation
            , BinderCvLicensesCertification binderCvLicensesCertification
            , BinderCvProfessionalAffiliations binderCvProfessionalAffiliations
            , BinderCvSkills binderCvSkills
            , @RequestParam("career_company") String career_company[]
            , @RequestParam("career_company_address") String career_company_address[]
            , @RequestParam("career_period") String career_period[]
            , @RequestParam("career_position") String career_position[]
            , @RequestParam("career_team") String career_team[]
            , @RequestParam("experience_name") String experience_name[]
            , @RequestParam("experience_content") String experience_content[]
            , BindingResult result) {

        if(result.hasErrors()) {
            return "binder/add";
        }


        BinderCv oldBinderCv = binderCvService.getUserActiveCv(SessionUtil.getUserId());

        if (oldBinderCv != null) {

            oldBinderCv.setIsActive("0");
            oldBinderCv = binderCvService.save(oldBinderCv);

            BinderCv binderCv = new BinderCv();
            binderCv.setRegDate(DateUtil.getTodayString());
            binderCv.setIsActive("1");
            binderCv.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
            binderCv.setVer(oldBinderCv.getVer() + 1);

            BinderCv binderCv1 = binderCvService.save(binderCv);

            binderCvEducation.setBinderCv(binderCv1);
            binderCvEducationService.save(binderCvEducation);

            for(int i=0; i<career_company.length; i++) {
                if (!career_company[i].isEmpty()) {
                    BinderCvCareerHistory obj = new BinderCvCareerHistory();
                    obj.setCompany(career_company[i]);
                    obj.setCompanyAddress(career_company_address[i]);
                    obj.setPeriod(career_period[i]);
                    obj.setPosition(career_position[i]);
                    obj.setTeamDepartment(career_team[i]);
                    obj.setBinderCv(binderCv1);
                    binderCvCareerHistoryService.save(obj);
                }
            }

            binderCvLicensesCertification.setBinderCv(binderCv1);
            binderCvLicensesCertificationService.save(binderCvLicensesCertification);

            binderCvProfessionalAffiliations.setBinderCv(binderCv1);
            binderCvProfessionalAffiliationsService.save(binderCvProfessionalAffiliations);

            binderCvSkills.setBinderCv(binderCv1);
            binderCvSkillsService.save(binderCvSkills);

            for(int i=0; i<experience_name.length; i++) {
                if(!experience_name[i].isEmpty()) {
                    BinderCvExperience obj = new BinderCvExperience();
                    obj.setName(experience_name[i]);
                    obj.setContent(experience_content[i]);
                    obj.setBinderCv(binderCv1);
                    binderCvExperienceService.save(obj);
                }
            }
        }


        //objService.save(obj);

        return "redirect:/binder/cv/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        BinderCv obj = binderCvService.getById(id);

        binderCvService.delete(obj);

        return "redirect:/binder/cv/list";
    }
}
