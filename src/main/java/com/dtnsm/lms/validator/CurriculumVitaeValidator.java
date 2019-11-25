package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVCareerHistory;
import com.dtnsm.lms.domain.CVEducation;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CurriculumVitaeValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;

        int eduIndex = 0;
        int eduSize = cv.getEducations().size();
        for(CVEducation edu : cv.getEducations()) {
            //마지막 row만 present 선택 가능
            if(edu.isPresent() && (eduIndex + 1 < eduSize)) {
                edu.setPresent(false);
            }
            if(ObjectUtils.isEmpty(edu.getStartDate())) {
                errors.rejectValue("educations[" + eduIndex + "].startDate", "message.required", "required field.");
            }

            if(edu.isPresent() == false && ObjectUtils.isEmpty(edu.getEndDate())) {
                errors.rejectValue("educations[" + eduIndex + "].startDate", "message.required", "required field.");
            }

            if(StringUtils.isEmpty(edu.getNameOfUniversity())) {
                errors.rejectValue("educations[" + eduIndex + "].nameOfUniversity", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getCityCountry())) {
                errors.rejectValue("educations[" + eduIndex + "].cityCountry", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getDegree())) {
                errors.rejectValue("educations[" + eduIndex + "].degree", "message.required", "required field.");
            }
//            if(StringUtils.isEmpty(edu.getNameOfSupervisor())) {
//                errors.rejectValue("educations[" + eduIndex + "].nameOfSupervisor", "message.required", "required field.");
//            }

            eduIndex ++;
        }

        int historyIndex = 0;
        int historySize = cv.getCareerHistories().size();
        for(CVCareerHistory history : cv.getCareerHistories()) {
            //마지막 row만 present 선택 가능
            if(history.isPresent() && (historyIndex + 1 < historySize)) {
                history.setPresent(false);
            }
            if(ObjectUtils.isEmpty(history.getStartDate())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].startDate", "message.required", "required field.");
            }

            if(history.isPresent() == false && ObjectUtils.isEmpty(history.getEndDate())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].startDate", "message.required", "required field.");
            }

            if(StringUtils.isEmpty(history.getCompanyName())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].companyName", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(history.getCityCountry())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].cityCountry", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(history.getPosition())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].position", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(history.getTeamDepartment())) {
                errors.rejectValue("careerHistories[" + historyIndex + "].teamDepartment", "message.required", "required field.");
            }

            historyIndex ++;
        }
    }
}
