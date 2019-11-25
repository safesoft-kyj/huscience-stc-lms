package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CVCareerHistory;
import com.dtnsm.lms.domain.CVEducation;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.repository.CVCareerHistoryRepository;
import com.dtnsm.lms.repository.CVEducationRepository;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
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

        return savedCV;
    }


}
