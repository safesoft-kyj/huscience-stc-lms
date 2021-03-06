package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.data.CVCodeList;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.DegreeType;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CurriculumVitaeService;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.validator.*;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.dto.CV;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

@Controller
@RequestMapping("/mypage")
@SessionAttributes({"pageInfo", "cv", "phaseList", "taList", "roleList", "indicationMap", "universityList", "cityCountryList", "countryList", "skillLanguageList", "skillCertificationList", "degreeType"})
@RequiredArgsConstructor
@Slf4j
public class MyPageCvJdController {
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final EducationValidator educationValidator;
    private final CareerHistoryValidator careerHistoryValidator;
    private final LicenseCertificationValidator licenseCertificationValidator;
    private final MembershipValidator membershipValidator;
    private final ExperienceValidator experienceValidator;
    private final SkillValidator skillValidator;
    private final CurriculumVitaeService curriculumVitaeService;
    private final SignatureRepository signatureRepository;
    private final FileUploadProperties prop;
    private final DocumentConverter documentConverter;
    private final CVCodeList cvCodeList;
    private final CurriculumVitaeReportService curriculumVitaeReportService;
    private final UserRepository userRepository;
    private final MailService mailService;

    private PageInfo pageInfo = new PageInfo();

    @Value("${site.code}")
    private String siteCode;

    @Value("${my.cv}")
    private String mypageCv;

    @Value("${my.jd}")
    private String mypageJd;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("???????????????");
    }

    @GetMapping("/cv")
    public String cv(Model model) {
        String userId = SessionUtil.getUserId();
        log.debug("@?????? CV ????????? ????????????. ({})", userId);
        Optional<CurriculumVitae> currentCV = latestCV(userId);
        if(currentCV.isPresent()) {
            CurriculumVitae cv = currentCV.get();
            log.debug("<== CV({}) status : {}", userId, cv.getStatus());
            if(cv.getStatus() == CurriculumVitaeStatus.CURRENT) {
                pageInfo.setPageId("m-mypage-cv");
                pageInfo.setPageTitle(mypageCv);

                model.addAttribute(pageInfo);
                model.addAttribute("cv", cv);

                return "content/mypage/cv/current";
            } else {
                return "redirect:/mypage/cv/" + currentCV.get().getId() + "/preview";
            }
        } else {
            return "redirect:/mypage/cv/edit";
        }
    }

    @GetMapping("/cv/old")
    public String oldVersion(Model model) {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle(mypageCv);
        Account account = SessionUtil.getUserDetail().getUser();
        model.addAttribute(pageInfo);

        BooleanBuilder builder = new BooleanBuilder();
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        builder.and(qCurriculumVitae.account.userId.eq(account.getUserId()));
        builder.and(qCurriculumVitae.status.eq(CurriculumVitaeStatus.SUPERSEDED));
        model.addAttribute("oldVersions", curriculumVitaeRepository.findAll(builder, qCurriculumVitae.id.desc()));

        return "content/mypage/cv/old";
    }

    @GetMapping("/cv/{newOrEdit}")
    public String newCV(@PathVariable("newOrEdit") String newOrEdit, @RequestParam(value = "id", required = false) Integer id, Model model) throws Exception {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle(mypageCv);

        Account account = SessionUtil.getUserDetail().getUser();

        model.addAttribute(pageInfo);
        model.addAttribute("degreeType", DegreeType.getDegreeTypes());
        model.addAttribute("taList", cvCodeList.getTaList());
        model.addAttribute("indicationMap", cvCodeList.getIndicationMap());
        model.addAttribute("roleList", cvCodeList.getRoleList());
        model.addAttribute("phaseList", cvCodeList.getPhaseList());
        model.addAttribute("universityList", cvCodeList.getUniversityList());
        model.addAttribute("cityCountryList", cvCodeList.getCityCountryList());
        model.addAttribute("countryList", cvCodeList.getCountryList());
        model.addAttribute("skillLanguageList", cvCodeList.getSkillLanguages());
        model.addAttribute("skillCertificationList", cvCodeList.getSkillCertifications());

        CurriculumVitae cv;
        if(!ObjectUtils.isEmpty(id)) {
            CurriculumVitae orgCV = curriculumVitaeRepository.findById(id).get();
            cv = (CurriculumVitae) orgCV.clone();
            if("new".equals(newOrEdit)) {
                cv.setParentId(orgCV.getId());
                cv.setId(null);
                cv.setInitial(false);
                cv.setStatus(CurriculumVitaeStatus.NEW);
                cv.getEducations().forEach(e -> e.setReadOnly(true));
                cv.getCareerHistories().forEach(c -> c.setReadOnly(true));
                cv.getLicenses().forEach(i -> i.setReadOnly(true));
                cv.getCertifications().forEach(i -> i.setReadOnly(true));
                cv.getMemberships().forEach(i -> i.setReadOnly(true));
                cv.getLanguages().forEach(i -> i.setReadOnly(true));
                cv.getComputerKnowledges().forEach(i -> i.setReadOnly(true));
                cv.getExperiences().forEach(i -> i.setReadOnly(true));
                cv.setReviewed(false);
                cv.setReviewedDate(null);
                cv.setCvFileName(null);
                cv.setHtmlContent(null);
            }
        } else {
            //TEST
            log.info("Department add");


            cv = new CurriculumVitae();
            cv.setInitial(true);
            cv.getEducations().add(new CVEducation());

            CVCareerHistory history = new CVCareerHistory();
            history.setPresent(true);
            history.setCityCountry("Seoul, Korea");
            history.setClinicalTrialExperience(true);
            history.setCompanyName(siteCode);
            if(!StringUtils.isEmpty(account.getIndate())) {
                history.setStartDate(DateUtil.getStringToDate(account.getIndate(), "yyyy-MM-dd"));
            }
            if(!ObjectUtils.isEmpty(account.getIndate())) {
                history.setStartDate(DateUtil.getStringToDate(account.getIndate()));
            }
            history.getCvTeamDepts().add(new CVTeamDept());
            cv.getCareerHistories().add(history);
            cv.getLicenses();//.add(new CVLicense());
            cv.getCertifications();//.add(new CVCertification());
            cv.getMemberships();//.add(new CVMembership());
            cv.getLanguages().add(new CVLanguage());
            CVComputerKnowledge computerKnowledge = new CVComputerKnowledge();
            computerKnowledge.setProgramName("MS Word, MS Excel, MS PowerPoint");
            cv.getComputerKnowledges().add(computerKnowledge);
            cv.getExperiences();//.add(new CVExperience());
        }

        model.addAttribute("cv", cv);

        return "content/mypage/cv/edit";
    }

//    @GetMapping("/cv/edit")
//    public String cv(@RequestParam(value = "id", required = false) Integer id, Model model) throws Exception {
//        pageInfo.setPageId("m-mypage-cv");
//        pageInfo.setPageTitle("Curriculum Vitae");
//
//        model.addAttribute(pageInfo);
//
//        model.addAttribute("indicationList", indicationRepository.findAll(QCVIndication.cVIndication.indication.asc()));
//        model.addAttribute("phaseList", phaseRepository.findAll(QCVPhase.cVPhase.phase.asc()));
//        CurriculumVitae cv;
//        if(ObjectUtils.isEmpty(id)) {
//            cv = new CurriculumVitae();
//            cv.setInitial(true);
//            cv.getEducations().add(new CVEducation());
//
//            CVCareerHistory history = new CVCareerHistory();
//            history.setPresent(true);
//            history.setCityCountry("Seoul, Korea");
//            history.setClinicalTrialExperience(true);
//            history.setCompanyName("Dt&SanoMedics");
//            Account account = SessionUtil.getUserDetail().getUser();
//            if(!ObjectUtils.isEmpty(account.getIndate())) {
//                history.setStartDate(DateUtil.getStringToDate(account.getIndate()));
//            }
//            cv.getCareerHistories().add(history);
//            cv.getLicenses().add(new CVLicense());
//            cv.getCertifications().add(new CVCertification());
//            cv.getMemberships().add(new CVMembership());
//            cv.getLanguages().add(new CVLanguage());
//            cv.getComputerKnowledges().add(new CVComputerKnowledge());
//            cv.getExperiences().add(new CVExperience());
//        } else {
//            CurriculumVitae savedCV = getCV(id).get();
//
//            cv = (CurriculumVitae)savedCV.clone();
//
//            for (CVLanguage language : cv.getLanguages()) {
//                System.out.println("@Language => " + language);
//                language.getLanguageCertifications().forEach(c -> {
//                    System.out.println("@Program : " + c.getCertificateProgram());
//                });
//            }
//
//
////            for(int i = 0; i < savedCV.getLanguages().size(); i ++) {
////                for(int k = 0; k < savedCV.getLanguages().get(i).getLanguageCertifications().size(); k ++) {
////                    cv.getLanguages().get(i).getLanguageCertifications().set(k, savedCV.getLanguages().get(i).getLanguageCertifications().get(k));
////                }
////            }
//            //LazyInitializationException ?????? ?????? ?????????..
////            cv.getLanguages().forEach(lang -> lang.getLanguageCertifications());
////            cv.getComputerKnowledges().forEach(com -> com.getComputerCertifications());
//        }
//
//        model.addAttribute("cv", cv);
//
//        return "content/mypage/cv/edit";
//    }

    @Transactional
    @PostMapping("/cv/edit")
    public String cv(@ModelAttribute("cv") CurriculumVitae cv, BindingResult result,
                     SessionStatus sessionStatus, HttpServletRequest request,
                     @RequestParam("currentPos") Integer currentPos,
                     @RequestParam(value = "goto", required = false) Integer goIndex,
                     Model model) throws Exception {
        boolean isAdd = WebUtils.hasSubmitParameter(request, "add");
        boolean isRemove = WebUtils.hasSubmitParameter(request, "remove");
        boolean isPrev = WebUtils.hasSubmitParameter(request, "prev");
        boolean isNext = WebUtils.hasSubmitParameter(request, "next");

        log.info("CV Info : {}", cv.getCareerHistories());

        if(!ObjectUtils.isEmpty(goIndex)) {
            int pos = currentPos.intValue();
            switch (pos) {
                case 0:
                    educationValidator.validate(cv, result);
                    break;
                case 1:
                    careerHistoryValidator.validate(cv, result);
                    break;
                case 2:
                    licenseCertificationValidator.validate(cv, result);
                    break;
                case 3:
                    membershipValidator.validate(cv, result);
                    break;
                case 4:
                    skillValidator.validate(cv, result);
                    break;
                case 5:
                    experienceValidator.validate(cv, result);
                    break;
            }

            if(result.hasErrors()) {
                log.info("<== validation error : {}", result.getAllErrors());
            } else {
                cv.setPos(goIndex.intValue());
            }
            return "content/mypage/cv/edit";
        }

        if(isPrev) {
            cv.setPos(ServletRequestUtils.getIntParameter(request, "prev"));
            return "content/mypage/cv/edit";
        } else if(isNext) {
            if(cv.getPos() == 0) {
                educationValidator.validate(cv, result);
            } else if(cv.getPos() == 1) {
                careerHistoryValidator.validate(cv, result);
            } else if(cv.getPos() == 2) {
                licenseCertificationValidator.validate(cv, result);
            } else if(cv.getPos() == 3) {
                membershipValidator.validate(cv, result);
            } else if(cv.getPos() == 4) {
                skillValidator.validate(cv, result);
            }

            if(result.hasErrors()) {
                log.info("<== validation error : {}", result.getAllErrors());
            } else {
                cv.setPos(ServletRequestUtils.getIntParameter(request, "next"));
            }
            return "content/mypage/cv/edit";
        }

//        log.debug("@CV.id : {}", cv.getId());
        if(isAdd) {
            String target = ServletRequestUtils.getStringParameter(request, "add");
            int index = -1;
            if(target.indexOf(":") != -1) {
                String[] s = target.split(":");
                target = s[0];
                index = Integer.parseInt(s[1]);
            }

            model.addAttribute("target", target);
            switch (target) {
                case "education":
                    cv.getEducations().add(new CVEducation());
                    break;
                case "careerHistory":
                    CVCareerHistory careerHistory = new CVCareerHistory();
                    careerHistory.getCvTeamDepts().add(new CVTeamDept());
                    cv.getCareerHistories().add(careerHistory);
                    break;
                case "teamDept":
                    cv.getCareerHistories().get(index).getCvTeamDepts().add(new CVTeamDept());
                    break;
                case "license":
                    cv.getLicenses().add(new CVLicense());
                    break;
                case "certification":
                    cv.getCertifications().add(new CVCertification());
                    break;
                case "membership":
                    cv.getMemberships().add(new CVMembership());
                    break;
                case "language":
                    cv.getLanguages().add(new CVLanguage());
                    break;
                case "languageCertification":
                    cv.getLanguages().get(index).getLanguageCertifications().add(new CVLanguageCertification());
                    break;
                case "computerKnowledge":
                    cv.getComputerKnowledges().add(new CVComputerKnowledge());
                    break;
                case "computerCertification":
                    cv.getComputerKnowledges().get(index).getComputerCertifications().add(new CVComputerCertification());
                    break;
                case "experience":
                    cv.getExperiences().add(new CVExperience());
                    break;
            }

            return "content/mypage/cv/edit";
        } else if(isRemove) {
            String target = ServletRequestUtils.getStringParameter(request,"remove");
            log.info("@cv remove Target : {}", target);
            String[] s = target.split(":");
            boolean isSaved = !ObjectUtils.isEmpty(cv.getId());
            int index = -1;
            String[] sindex = null;
            if(s[1].indexOf(".") == -1) {
                index = Integer.parseInt(s[1]);
            } else {
                sindex = s[1].split("\\.");
                index = Integer.parseInt(sindex[0]);
            }

            switch(s[0]) {
                case "education":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getEducations().get(index).getId())) {
                            cv.getRemoveEducations().add(cv.getEducations().get(index));
                        }
                    }
                    cv.getEducations().remove(index);
                    break;
                case "careerHistory":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getCareerHistories().get(index).getId())) {
                            cv.getRemoveCareerHistories().add(cv.getCareerHistories().get(index));
                        }
                    }
                    cv.getCareerHistories().remove(index);
                    break;
                case "teamDept":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getCareerHistories().get(index).getCvTeamDepts().get(Integer.parseInt(sindex[1])))) {
                            cv.getCareerHistories().get(index).getRemoveCvTeamDepts().add(
                                    cv.getCareerHistories().get(index).getCvTeamDepts().get(Integer.parseInt(sindex[1])
                            ));
                        }
                    }
                    cv.getCareerHistories().get(index).getCvTeamDepts().remove(Integer.parseInt(sindex[1]));
                    break;
                case "license":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getLicenses().get(index).getId())) {
                            cv.getRemoveLicenses().add(cv.getLicenses().get(index));
                        }
                    }
                    cv.getLicenses().remove(index);
                    break;
                case "certification":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getCertifications().get(index).getId())) {
                            cv.getRemoveCertifications().add(cv.getCertifications().get(index));
                        }
                    }
                    cv.getCertifications().remove(index);
                    break;
                case "membership":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getMemberships().get(index).getId())) {
                            cv.getRemoveMemberships().add(cv.getMemberships().get(index));
                        }
                    }
                    cv.getMemberships().remove(index);
                    break;
                case "language":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getLanguages().get(index).getId())) {
                            cv.getRemoveLanguages().add(cv.getLanguages().get(index));
                        }
                    }
                    cv.getLanguages().remove(index);
                    break;
                case "languageCertification":
//                    String[] sindex = s[1].split("\\.");
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getLanguages().get(index).getLanguageCertifications().get(Integer.parseInt(sindex[1])).getId())) {
                            cv.getLanguages().get(Integer.parseInt(sindex[0])).getRemoveLanguageCertifications().add(cv.getLanguages().get(Integer.parseInt(sindex[0])).getLanguageCertifications().get(Integer.parseInt(sindex[1])));
                        }
                    }
                    cv.getLanguages().get(Integer.parseInt(sindex[0])).getLanguageCertifications().remove(Integer.parseInt(sindex[1]));
                    break;
                case "computerKnowledge":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getComputerKnowledges().get(index).getId())) {
                            cv.getRemoveComputerKnowledges().add(cv.getComputerKnowledges().get(index));
                        }
                    }
                    cv.getComputerKnowledges().remove(index);
                    break;
                case "computerCertification":
                    String[] cindex = s[1].split("\\.");
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getComputerKnowledges().get(Integer.parseInt(cindex[0])).getComputerCertifications().get(Integer.parseInt(cindex[1])).getId())) {
                            cv.getComputerKnowledges().get(Integer.parseInt(cindex[0])).getRemoveComputerCertifications().add(cv.getComputerKnowledges().get(Integer.parseInt(cindex[0])).getComputerCertifications().get(Integer.parseInt(cindex[1])));
                        }
                    }
                    cv.getComputerKnowledges().get(Integer.parseInt(cindex[0])).getComputerCertifications().remove(Integer.parseInt(cindex[1]));
                    break;
                case "experience":
                    if(isSaved) {
                        if(!ObjectUtils.isEmpty(cv.getExperiences().get(index).getId())) {
                            cv.getRemoveExperiences().add(cv.getExperiences().get(index));
                        }
                    }
                    cv.getExperiences().remove(index);
                    break;
            }

            model.addAttribute("target", s[0]);

            return "content/mypage/cv/edit";
        }

        boolean isSubmit = WebUtils.hasSubmitParameter(request, "_submit");

        //Registration ?????? ??? ???,
        if(isSubmit) {
            experienceValidator.validate(cv, result);
            if(result.hasErrors()) {
                return "content/mypage/cv/edit";
            }

            cv.setStatus(CurriculumVitaeStatus.TEMP);
            cv.setAccount(SessionUtil.getUserDetail().getUser());
            if (ObjectUtils.isEmpty(cv.getId())) {
                cv.setInitial(countCV(SessionUtil.getUserId()) == 0 ? true : false);
            }

//            if (status != CurriculumVitaeStatus.TEMP) {
            Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
            cv.setBase64sign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
//                cv.setSignDate(new Date());
//            }

            CurriculumVitae savedCV = curriculumVitaeService.save(cv);
            log.info("=> UserId : {}, CV??? ?????? ???????????????. CV ID : {}", SessionUtil.getUserId(), savedCV.getId());


            sessionStatus.setComplete();
            return "redirect:/mypage/cv/" + savedCV.getId() + "/preview";
        } else {
            throw new RuntimeException("????????? ?????? ?????????.");
        }
    }

    @GetMapping("/cv/{id}/preview")
    public String preview(@PathVariable("id") Integer id, Model model) {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle(mypageCv);
        model.addAttribute(pageInfo);
        model.addAttribute("id", id);
        return "content/mypage/cv/preview";
    }

    @PostMapping("/cv/{id}/preview")
    public String published(@PathVariable("id") Integer id) throws Exception {
        CurriculumVitae cv = curriculumVitaeRepository.findById(id).get();

        CV dto = curriculumVitaeService.toCV(cv, false);
        String outputFileName = SessionUtil.getUserId() + "_CV_" + id + ".pdf";
        Files.createDirectories(Paths.get(prop.getBinderDir()).toAbsolutePath().normalize());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        boolean result = curriculumVitaeReportService.assembleDocument(dto, os);
        log.info("CV Id : {}, Generate Result : {}", id, result);
        if(result) {
            log.info("CV ID : {}, Word to PDF...", id);
            File outputPdf = new File(prop.getBinderDir() + outputFileName);
            documentConverter.word2pdf(new ByteArrayInputStream(os.toByteArray()), new FileOutputStream(outputPdf));
            log.info("CV ID : {}, Word to PDF...Done.", id);

            cv.setStatus(CurriculumVitaeStatus.CURRENT);
            cv.setCvFileName(outputFileName);
            CurriculumVitae savedCV = curriculumVitaeRepository.save(cv);

            if(!ObjectUtils.isEmpty(savedCV.getParentId())) {
                CurriculumVitae parentCV = curriculumVitaeRepository.findById(savedCV.getParentId()).get();
                parentCV.setStatus(CurriculumVitaeStatus.SUPERSEDED);
                curriculumVitaeRepository.save(parentCV);
            }
        }
        return "redirect:/mypage/cv";
    }

    @GetMapping("/cv/{id}/download")
    public ResponseEntity<Resource> downloadCV(@PathVariable("id") Integer id) {
        try {
            Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findById(id);
            if(optionalCurriculumVitae.isPresent()) {
//                Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

                CurriculumVitae cv = optionalCurriculumVitae.get();
//                String filePath = prop.getBinderDir() + cv.getCvFileName();
                Path fileStorageLocation = Paths.get(prop.getBinderDir()).toAbsolutePath().normalize();
                Path filePath = fileStorageLocation.resolve(cv.getCvFileName()).normalize();
                log.info("@CV Id :{}, Download : {}", cv.getId(), filePath.toUri());
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("application/octet-stream"))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"CV_" + cv.getAccount().getComNum() + "("+cv.getInitialName()+").pdf\"")
                            .body(resource);
                } else {
                    throw new RuntimeException("File not found CV ID : " + id);
                }
            } else {
                throw new RuntimeException("File not found CV ID : " + id);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @GetMapping("/cv/{id}/generate")
    @ResponseBody
    @Transactional
    public void generate(@PathVariable("id") Integer id, HttpServletResponse response) {
        try {
            CurriculumVitae cv = curriculumVitaeRepository.findById(id).get();
            CV dto = curriculumVitaeService.toCV(cv, false);
//                    response.setHeader("Content-Disposition", "attachment; filename=\"cv.pdf\"");
//                    response.setContentType("application/pdf");
//            String outputFileName = "CV_"+savedCV.getId()+"_" + SessionUtil.getUserId() + ".docx";
//            Files.createDirectories(Paths.get(prop.getCvUploadDir()).toAbsolutePath().normalize());
//                        curriculumVitaeReportService.generateReport(dto, prop.getCvUploadDir() + outputFileName, savedCV.getId());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean result = curriculumVitaeReportService.assembleDocument(dto, os);
            log.info("CV Id : {}, Generate Result : {}", id, result);
            if(result) {
                log.info("CV ID : {}, Word to HTML...", id);
                ByteArrayOutputStream htmlOutput = new ByteArrayOutputStream();
                documentConverter.toHTML(new ByteArrayInputStream(os.toByteArray()), htmlOutput);
                byte[] html = htmlOutput.toByteArray();
                cv.setHtmlContent(new String(html, "utf-8"));
                log.info("CV ID : {}, Word to HTML...Done.", id);
                curriculumVitaeRepository.save(cv);
                log.info("CV ID : {}, CV Html Content Update.", id);
                OutputStream out = response.getOutputStream();
                out.write(html);
                out.flush();
                out.close();
            } else {
                response.sendError(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("CV Id : {}, error : {}", id, e);
        }
    }


    protected Optional<CurriculumVitae> getCV(Integer id) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qCurriculumVitae.account.userId.eq(userId));
//        builder.and(qCurriculumVitae.status.eq(status));
        builder.and(qCurriculumVitae.id.eq(id));
        return curriculumVitaeRepository.findOne(builder);
    }

    protected Optional<CurriculumVitae> getCV(String userId, CurriculumVitaeStatus status) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        builder.and(qCurriculumVitae.status.eq(status));
        return curriculumVitaeRepository.findOne(builder);
    }

    protected Optional<CurriculumVitae> latestCV(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        Iterable<CurriculumVitae> iterable = curriculumVitaeRepository.findAll(builder, qCurriculumVitae.id.desc());
        Iterator<CurriculumVitae> iter = iterable.iterator();
        if(iter.hasNext()) {
            return Optional.of(iter.next());
        } else {
            return Optional.empty();
        }
    }

    protected long countCV(String userId, CurriculumVitaeStatus status) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        builder.and(qCurriculumVitae.status.eq(status));
        return curriculumVitaeRepository.count(builder);
    }

    protected long countCV(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        return curriculumVitaeRepository.count(builder);
    }

    @GetMapping("/jd/{status}")
    public String jd(@PathVariable(value = "status") String stringStatus, Model model) {
        pageInfo.setPageId("m-mypage-jd");
        pageInfo.setPageTitle("Job Description");

        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(SessionUtil.getUserId()));
        JobDescriptionStatus status = JobDescriptionStatus.valueOf(stringStatus.toUpperCase());
        if(status == JobDescriptionStatus.SUPERSEDED) {
            builder.and(qUserJobDescription.status.in(Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        } else {
            builder.and(qUserJobDescription.status.in(Arrays.asList(JobDescriptionStatus.APPROVED, JobDescriptionStatus.AGREE, JobDescriptionStatus.ASSIGNED)));
        }
        Iterable<UserJobDescription> userJobDescriptions = userJobDescriptionRepository.findAll(builder, qUserJobDescription.createdDate.desc());

        model.addAttribute(pageInfo);
        model.addAttribute("userJobDescriptions", userJobDescriptions);
        return "content/mypage/jd/list";
    }

//    @GetMapping("/jd/{id}/view")
//    public void jdView(@PathVariable(value = "id") Integer id, HttpServletResponse res) throws Exception {
//
//        UserJobDescription userJobDescription = userJobDescriptionRepository.findById(id).get();
//        JobDescriptionVersionFile file = userJobDescription.getJobDescriptionVersion().getJobDescriptionVersionFile();
//
//        res.setContentType("text/html;charset=utf-8");
//        InputStream is = new FileInputStream(new File(prop.getBinderJdUploadDir() + "/" + file.getFileName()+".html"));
//        OutputStream os = res.getOutputStream();
//        os.write(is.readAllBytes());
//        os.flush();
//        os.close();
//    }

    @PostMapping("/jd")
    public String agreeJd(@RequestParam("id") Integer id) {
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setAgreeDate(new Date());
            userJobDescription.setStatus(JobDescriptionStatus.AGREE);
            userJobDescription.setAgreeSign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");

            userJobDescriptionRepository.save(userJobDescription);

            //TODO Job Description (?????? ??????)
            Account account = userRepository.findByUserId(userJobDescription.getUsername());
            if(!StringUtils.isEmpty(account.getParentUserId())) {
                String toEmail = userRepository.findByUserId(account.getParentUserId()).getEmail();
                log.info("??????????????? JD ?????? ?????? ?????? ?????? : {}", toEmail);
                String jobTitle = userJobDescription.getJobDescriptionVersion().getJobDescription().getTitle();
                Context context = new Context();
                context.setVariable("jobTitle", jobTitle);
                context.setVariable("empName", account.getName());
                mailService.send(toEmail, String.format(BinderAlarmType.JD_AGREE.getTitle(), jobTitle, account.getName()), BinderAlarmType.JD_AGREE, context);
            } else {
                log.error("????????? ????????? ?????? ?????? ????????????.");
            }

        }

        return "redirect:/mypage/jd/approved";
    }
}
