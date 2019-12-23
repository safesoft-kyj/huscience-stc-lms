package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.JobDescriptionVersionFile;
import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.common.utils.Base64Utils;
import com.dtnsm.lms.data.CVCodeList;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.service.CurriculumVitaeService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.validator.*;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.dto.*;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;

@Controller
@RequestMapping("/mypage")
@SessionAttributes({"pageInfo", "cv", "phaseList", "taList", "roleList", "indicationMap", "universityList", "cityCountryList", "countryList", "skillLanguageList", "skillCertificationList"})
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

    private PageInfo pageInfo = new PageInfo();

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    @GetMapping("/cv")
    public String cv(Model model) {
        String userId = SessionUtil.getUserId();
        log.debug("@최신 CV 정보를 가져온다. ({})", userId);
        Optional<CurriculumVitae> currentCV = latestCV(userId);
        if(currentCV.isPresent()) {
            CurriculumVitae cv = currentCV.get();
            log.debug("<== CV({}) status : {}", userId, cv.getStatus());
            if(cv.getStatus() == CurriculumVitaeStatus.CURRENT) {
                pageInfo.setPageId("m-mypage-cv");
                pageInfo.setPageTitle("Curriculum Vitae");

                model.addAttribute(pageInfo);
                model.addAttribute("cv", cv);
                return "content/mypage/cv/current";
            } else {
                return "redirect:/mypage/cv/edit?id=" + currentCV.get().getId();
            }
        } else {
            return "redirect:/mypage/cv/edit";
        }
    }

    @GetMapping("/cv/old")
    public String oldVersion(Model model) {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle("Curriculum Vitae");

        model.addAttribute(pageInfo);

        BooleanBuilder builder = new BooleanBuilder();
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        builder.and(qCurriculumVitae.status.eq(CurriculumVitaeStatus.SUPERSEDED));
        model.addAttribute("oldVersions", curriculumVitaeRepository.findAll(builder, qCurriculumVitae.id.desc()));

        return "content/mypage/cv/old";
    }

    @GetMapping("/cv/{newOrEdit}")
    public String newCV(@PathVariable("newOrEdit") String newOrEdit, @RequestParam(value = "id", required = false) Integer id, Model model) throws Exception {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle("Curriculum Vitae");

        Account account = SessionUtil.getUserDetail().getUser();

        model.addAttribute(pageInfo);

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
            }
        } else {
            cv = new CurriculumVitae();
            cv.setInitial(true);
            cv.getEducations().add(new CVEducation());

            CVCareerHistory history = new CVCareerHistory();
            history.setPresent(true);
            history.setCityCountry("Seoul, Korea");
            history.setClinicalTrialExperience(true);
            history.setCompanyName("Dt&SanoMedics");
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
//            //LazyInitializationException 발생 하지 않도록..
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
                     SessionStatus sessionStatus, HttpServletRequest request, Model model) throws Exception {
        boolean isAdd = WebUtils.hasSubmitParameter(request, "add");
        boolean isRemove = WebUtils.hasSubmitParameter(request, "remove");
        boolean isPrev = WebUtils.hasSubmitParameter(request, "prev");
        boolean isNext = WebUtils.hasSubmitParameter(request, "next");

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

        log.debug("@CV.id : {}", cv.getId());
        if(isAdd) {
            String target = ServletRequestUtils.getStringParameter(request,"add");
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
//                Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
//                cv.setBase64sign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
//                cv.setSignDate(new Date());
//            }


            CurriculumVitae savedCV = curriculumVitaeService.save(cv);
            log.info("=> UserId : {}, CV가 저장 되었습니다. CV ID : {}", SessionUtil.getUserId(), savedCV.getId());



            sessionStatus.setComplete();
            return "redirect:/mypage/cv/" + savedCV.getId() + "/preview";
        } else {
            throw new RuntimeException("잘못된 요청 입니다.");
        }
    }

    @GetMapping("/cv/{id}/preview")
    public String preview(@PathVariable("id") Integer id, Model model) {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle("Curriculum Vitae");
        model.addAttribute(pageInfo);
        model.addAttribute("id", id);
        return "content/mypage/cv/preview";
    }

    @GetMapping("/cv/{id}/generate")
    @ResponseBody
    public void generate(@PathVariable("id") Integer id, HttpServletResponse response) {
        try {
            CurriculumVitae savedCV = curriculumVitaeRepository.findById(id).get();
            CV dto = new CV();
            dto.setEngName(savedCV.getAccount().getEngName());
            dto.setSignDate(!StringUtils.isEmpty(savedCV.getSignDate()) ? DateUtil.getDateToString(savedCV.getSignDate(), "dd-MMM-yyyy").toUpperCase() : null);
            if (!StringUtils.isEmpty(savedCV.getBase64sign())) {
                dto.setSign(new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(savedCV.getBase64sign()))));
            }

            dto.setEducations(savedCV.getEducations().stream().map(e ->
                    EducationDTO.builder()
                            .startDate(DateUtil.getDateToString(e.getStartDate(), "MMM yyyy"))
                            .endDate(e.isPresent() ? "Present" : DateUtil.getDateToString(e.getEndDate(), "MMM yyyy"))
                            .nameOfUniversity(e.getNameOfUniversity())
                            .cityCountry(e.getCityCountry())
                            .bachelorsDegree(StringUtils.isEmpty(e.getBachelorsDegreeOther()) ? e.getBachelorsDegree() : e.getBachelorsDegreeOther())
                            .mastersDegree(StringUtils.isEmpty(e.getMastersDegreeOther()) ? e.getMastersDegree() : e.getMastersDegreeOther())
                            .mastersThesisTitle(e.getMastersThesisTitle())
                            .mastersName(e.getMastersName())
                            .phdDegree(e.getPhdDegree())
                            .phdName(e.getPhdName())
                            .build())
                    .collect(Collectors.toList()));

                    dto.setCareerHistories(savedCV.getCareerHistories().stream().map(c ->
                            CareerHistoryDTO.builder()
                                    .companyName(c.getCompanyName())
                                    .cityCountry("Others".equals(c.getCityCountry()) ? c.getCityCountryOther() : c.getCityCountry())
                                    .startDate(DateUtil.getDateToString(c.getStartDate(), "MMM yyyy"))
                                    .endDate(c.isPresent() ? "Present" : DateUtil.getDateToString(c.getEndDate(), "MMM yyyy"))
                                    .teamDeptDTOList(c.getCvTeamDepts()
                                            .stream()
                                            .map(cv -> TeamDeptDTO.builder().position(cv.getPosition())
                                            .team(cv.getTeam()).department(cv.getDepartment())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .build()
                    ).collect(Collectors.toList()));

            dto.setLicenses(savedCV.getLicenses().stream().map(i ->
                    LicenseDTO.builder()
                            .licenseNo(i.getLicenseNo())
                            .licenseInCountry(i.getLicenseInCountry())
                            .build()
            ).collect(Collectors.toList()));

            dto.setCertifications(savedCV.getCertifications().stream().map(i ->
                    CertificationDTO.builder()
                            .nameOfCertification(i.getNameOfCertification())
                            .organizers(i.getOrganizers())
                            .issueDate(DateUtil.getDateToString(i.getIssueDate(), "MMM YYYY").toUpperCase())
                            .build()
            ).collect(Collectors.toList()));

            dto.setMemberships(savedCV.getMemberships().stream().map(i ->
                    MembershipDTO.builder()
                            .name(i.getMembershipName())
                            .startYear(i.getStartYear())
                            .endYear(i.getEndYear())
                            .build()
            ).collect(Collectors.toList()));

            dto.setLanguages(savedCV.getLanguages().stream().map(i ->
                    LanguageDTO.builder()
                            .language(i.getLanguage())
                            .level(i.getLevel().getLabel())
                            .certificateProgramList(i.getLanguageCertifications().stream().map(c -> c.getCertificateProgram()).collect(Collectors.toList()))
                            .build()
            ).collect(Collectors.toList()));

            dto.setComputerKnowledges(savedCV.getComputerKnowledges().stream().map(i ->
                    ComputerKnowledgeDTO.builder()
                            .name(i.getProgramName())
                            .level(i.getLevel().getLabel())
                            .certificateProgramList(i.getComputerCertifications().stream().map(c -> c.getCertificateProgram()).collect(Collectors.toList()))
                            .build()
            ).collect(Collectors.toList()));

            dto.setExperiences(savedCV.getExperiences().stream().map(i ->
                    ExperienceDTO.builder()
                            .ta("Others".equals(i.getTa()) ? i.getTaOther() : i.getTa())
                            .indication("Others".equals(i.getIndication()) ? i.getIndicationOther() : i.getIndication())
                            .phase("Others".equals(i.getPhase()) ? i.getPhaseOther() : i.getPhase())
                            .role(i.getRole())
                            .globalOrLocal(i.getGlobalOrLocal().getLabel())
                            .workingDetails(i.getWorkingDetails())
                            .build()
            ).collect(Collectors.toList()));
//                    response.setHeader("Content-Disposition", "attachment; filename=\"cv.pdf\"");
//                    response.setContentType("application/pdf");
            String outputFileName = "CV_"+savedCV.getId()+"_" + SessionUtil.getUserId() + ".docx";
            Files.createDirectories(Paths.get(prop.getCvUploadDir()).toAbsolutePath().normalize());
//                        curriculumVitaeReportService.generateReport(dto, prop.getCvUploadDir() + outputFileName, savedCV.getId());
            boolean result = curriculumVitaeReportService.assembleDocument(dto, prop.getCvUploadDir() + outputFileName);
            log.info("output : {}{}, Generate Result : {}", prop.getCvUploadDir(), outputFileName, result);
            if(result) {
                documentConverter.toHTML(new FileInputStream(prop.getCvUploadDir() + outputFileName), response.getOutputStream());
            } else {
                response.sendError(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    @GetMapping("/jd/{id}/view")
    public void jdView(@PathVariable(value = "id") Integer id, HttpServletResponse res) throws Exception {

        UserJobDescription userJobDescription = userJobDescriptionRepository.findById(id).get();
        JobDescriptionVersionFile file = userJobDescription.getJobDescriptionVersion().getJobDescriptionVersionFile();

        res.setContentType("text/html;charset=utf-8");
        InputStream is = new FileInputStream(new File(prop.getBinderJdUploadDir() + "/" + file.getFileName()+".html"));
        OutputStream os = res.getOutputStream();
        os.write(is.readAllBytes());
        os.flush();
        os.close();
    }

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

            //TODO Job Description (동의 알림)
        }

        return "redirect:/mypage/jd/approved";
    }


}
