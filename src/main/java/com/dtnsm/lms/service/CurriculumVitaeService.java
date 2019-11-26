package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    public CurriculumVitae save(CurriculumVitae cv) {
        if(ObjectUtils.isEmpty(cv.getId())) {
            log.debug("@CV 신규 저장");
        } else {
            log.debug("@CV 수정");
        }

        CurriculumVitae savedCV = curriculumVitaeRepository.save(cv);
        for(CVEducation edu : cv.getEducations()) {
            edu.setCurriculumVitae(savedCV);
            cvEducationRepository.save(edu);
        }

        for(CVCareerHistory history : cv.getCareerHistories()) {
            history.setCurriculumVitae(cv);
            cvCareerHistoryRepository.save(history);
        }

        for(CVLicense license : cv.getLicenses()) {
            license.setCurriculumVitae(cv);
            cvLicenseRepository.save(license);
        }

        for(CVCertification certification : cv.getCertifications()) {
            certification.setCurriculumVitae(cv);
            cvCertificationRepository.save(certification);
        }

        for(CVMembership membership : cv.getMemberships()) {
            membership.setCurriculumVitae(cv);
            cvMembershipRepository.save(membership);
        }

        for(CVLanguage language : cv.getLanguages()) {
            language.setCurriculumVitae(cv);
            CVLanguage savedLanguage = cvLanguageRepository.save(language);

            for(CVLanguageCertification languageCertification : language.getLanguageCertifications()) {
                languageCertification.setLanguage(savedLanguage);
                cvLanguageCertificationRepository.save(languageCertification);
            }
        }

        for(CVComputerKnowledge computerKnowledge : cv.getComputerKnowledges()) {
            computerKnowledge.setCurriculumVitae(cv);
            CVComputerKnowledge savedComputerKnowledge = cvComputerKnowledgeRepository.save(computerKnowledge);

            for(CVComputerCertification computerCertification : computerKnowledge.getComputerCertifications()) {
                computerCertification.setComputerKnowledge(savedComputerKnowledge);
                cvComputerCertificationRepository.save(computerCertification);
            }
        }

        for(CVExperience experience : cv.getExperiences()) {
            experience.setCurriculumVitae(cv);
            cvExperienceRepository.save(experience);
        }

        return savedCV;
    }


}
