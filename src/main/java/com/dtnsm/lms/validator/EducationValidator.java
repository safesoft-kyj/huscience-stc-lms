package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVEducation;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EducationValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;

        if(StringUtils.isEmpty(cv.getInitialName())) {
            errors.rejectValue("initialName", "message.required", "required field.");
        }

        int eduIndex = 0;
//        int eduSize = cv.getEducations().size();
        for(CVEducation edu : cv.getEducations()) {
            //첫번째 row만 present 선택 가능
            if(edu.isPresent() && eduIndex > 0) {
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
    }
}
