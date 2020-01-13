package com.dtnsm.lms.service;

import com.dtnsm.common.utils.Base64Utils;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.repository.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.xdocreport.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j

public class CurriculumVitaeService {
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final CVEducationRepository cvEducationRepository;
    private final CVCareerHistoryRepository cvCareerHistoryRepository;
    private final CVLicenseRepository cvLicenseRepository;
    private final CVCertificationRepository cvCertificationRepository;
    private final CVMembershipRepository cvMembershipRepository;
    private final CVLanguageRepository cvLanguageRepository;
    private final CVLanguageCertificationRepository cvLanguageCertificationRepository;
    private final CVComputerKnowledgeRepository cvComputerKnowledgeRepository;
    private final CVComputerCertificationRepository cvComputerCertificationRepository;
    private final CVExperienceRepository cvExperienceRepository;
    private final CVTeamDeptRepository cvTeamDeptRepository;

    public CurriculumVitae save(CurriculumVitae cv) {
        boolean isNew = ObjectUtils.isEmpty(cv.getId());
        if(isNew) {
            log.debug("@CV 신규 저장");
        } else {
            log.debug("@CV 수정");

            if(!ObjectUtils.isEmpty(cv.getRemoveEducations())) {
                cvEducationRepository.deleteAll(cv.getRemoveEducations());
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveCareerHistories())) {
                cvCareerHistoryRepository.deleteAll(cv.getRemoveCareerHistories());
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveLicenses())) {
                cvLicenseRepository.deleteAll(cv.getRemoveLicenses());
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveCertifications())) {
                cvCertificationRepository.deleteAll(cv.getRemoveCertifications());
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveMemberships())) {
                cvMembershipRepository.deleteAll(cv.getRemoveMemberships());
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveLanguages())) {
                cvLanguageRepository.deleteAll(cv.getRemoveLanguages());
            }

            if(!ObjectUtils.isEmpty(cv.getLanguages())) {
                for(CVLanguage lang : cv.getLanguages()) {
                    if(!ObjectUtils.isEmpty(lang.getRemoveLanguageCertifications())) {
                        cvLanguageCertificationRepository.deleteAll(lang.getRemoveLanguageCertifications());
                    }
                }
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveComputerKnowledges())) {
                cvComputerKnowledgeRepository.deleteAll(cv.getRemoveComputerKnowledges());
            }

            if(!ObjectUtils.isEmpty(cv.getComputerKnowledges())) {
                for(CVComputerKnowledge com : cv.getComputerKnowledges()) {
                    if(!ObjectUtils.isEmpty(com.getRemoveComputerCertifications())) {
                        cvComputerCertificationRepository.deleteAll(com.getRemoveComputerCertifications());
                    }
                }
            }

            if(!ObjectUtils.isEmpty(cv.getRemoveExperiences())) {
                cvExperienceRepository.deleteAll(cv.getRemoveExperiences());
            }
        }

        if(!ObjectUtils.isEmpty(cv.getParentId())) {
            Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findById(cv.getParentId());
            CurriculumVitae oldCV = optionalCurriculumVitae.get();
            oldCV.setStatus(CurriculumVitaeStatus.SUPERSEDED);

            curriculumVitaeRepository.save(oldCV);//Superseded 처리

        }

        CurriculumVitae savedCV = curriculumVitaeRepository.save(cv);
        for(CVEducation edu : cv.getEducations()) {
            edu.setCurriculumVitae(savedCV);
            if(isNew) edu.setId(null);
            cvEducationRepository.save(edu);
        }

        for(CVCareerHistory history : cv.getCareerHistories()) {
            history.setCurriculumVitae(cv);
            if(isNew) history.setId(null);
            CVCareerHistory savedCareerHistory = cvCareerHistoryRepository.save(history);
            for(CVTeamDept cvTeamDept : history.getCvTeamDepts()) {
                cvTeamDept.setCareerHistory(savedCareerHistory);
                if(isNew) cvTeamDept.setId(null);
                cvTeamDeptRepository.save(cvTeamDept);
            }
        }

        for(CVLicense license : cv.getLicenses()) {
            license.setCurriculumVitae(cv);
            if(isNew) license.setId(null);
            cvLicenseRepository.save(license);
        }

        for(CVCertification certification : cv.getCertifications()) {
            certification.setCurriculumVitae(cv);
            if(isNew) certification.setId(null);
            cvCertificationRepository.save(certification);
        }

        for(CVMembership membership : cv.getMemberships()) {
            membership.setCurriculumVitae(cv);
            if(isNew) membership.setId(null);
            cvMembershipRepository.save(membership);
        }

        for(CVLanguage language : cv.getLanguages()) {
            language.setCurriculumVitae(cv);
            if(isNew) language.setId(null);
            CVLanguage savedLanguage = cvLanguageRepository.save(language);

            for(CVLanguageCertification languageCertification : language.getLanguageCertifications()) {
                languageCertification.setLanguage(savedLanguage);
                if(isNew) languageCertification.setId(null);
                cvLanguageCertificationRepository.save(languageCertification);
            }
        }

        for(CVComputerKnowledge computerKnowledge : cv.getComputerKnowledges()) {
            computerKnowledge.setCurriculumVitae(cv);
            if(isNew) computerKnowledge.setId(null);
            CVComputerKnowledge savedComputerKnowledge = cvComputerKnowledgeRepository.save(computerKnowledge);

            for(CVComputerCertification computerCertification : computerKnowledge.getComputerCertifications()) {
                computerCertification.setComputerKnowledge(savedComputerKnowledge);
                if(isNew) computerCertification.setId(null);
                cvComputerCertificationRepository.save(computerCertification);
            }
        }

        for(CVExperience experience : cv.getExperiences()) {
            experience.setCurriculumVitae(cv);
            if(isNew) experience.setId(null);
            cvExperienceRepository.save(experience);
        }

        return savedCV;
    }

    public CV toCV(CurriculumVitae savedCV, boolean name, boolean company) {

        CV dto = new CV();
        if(name) {
            dto.setEngName(savedCV.getInitialName());
        } else {
            dto.setEngName(savedCV.getAccount().getEngName());
        }
        dto.setSignDate(!StringUtils.isEmpty(savedCV.getSignDate()) ? DateUtil.getDateToString(savedCV.getSignDate(), "dd-MMM-yyyy").toUpperCase() : null);
        if (!StringUtils.isEmpty(savedCV.getBase64sign())) {
            dto.setSign(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(savedCV.getBase64sign())));
        }
        dto.setSignDate(DateUtil.getDateToString(new Date(), "dd-MMM-yyyy").toUpperCase());

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
                        .phdThesisTitle(e.getPhdThesisTitle())
                        .phdName(e.getPhdName())
                        .build())
                .collect(Collectors.toList()));

        dto.setCareerHistories(savedCV.getCareerHistories().stream().map(c ->
                CareerHistoryDTO.builder()
                        .companyName(company ? "***" : c.getCompanyName())
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
                        .licenseName(i.getNameOfLicense())
                        .licenseNo(i.getLicenseNo())
                        .licenseInCountry("Others".equals(i.getLicenseInCountry()) ? i.getLicenseInCountryOther() : i.getLicenseInCountry())
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
                        .language("Others".equals(i.getLanguage()) ? i.getLanguageOther() : i.getLanguage())
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
                        .roles(Stream.of(i.getRole()).map(r -> r.equals("Others") ? i.getRoleOther() : r).collect(Collectors.toList()))
                        .globalOrLocal(i.getGlobalOrLocal().getLabel())
                        .workingDetails(i.getWorkingDetails())
                        .build()
        ).collect(Collectors.toList()));

        return dto;
    }

}
