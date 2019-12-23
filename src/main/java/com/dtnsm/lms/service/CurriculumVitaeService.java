package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

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


}
