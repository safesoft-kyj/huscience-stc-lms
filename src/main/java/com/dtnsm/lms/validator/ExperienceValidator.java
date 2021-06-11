package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVExperience;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

@Component
public class ExperienceValidator implements Validator {
    private String otherString = "Others";
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;

        if(!ObjectUtils.isEmpty(cv.getExperiences())) {
            int size = cv.getExperiences().size();

            for(int i = 0; i < size; i ++) {
                CVExperience exp = cv.getExperiences().get(i);
                if(isOther(exp.getTa()) && StringUtils.isEmpty(exp.getTaOther())) {
                    errors.rejectValue("experiences[" + i + "].taOther", "message.required", "required field.");
                }
                if(isOther(exp.getIndication()) && StringUtils.isEmpty(exp.getIndicationOther())) {
                    errors.rejectValue("experiences[" + i + "].indicationOther", "message.required", "required field.");
                }
                if(isOther(exp.getPhase()) && StringUtils.isEmpty(exp.getPhaseOther())) {
                    errors.rejectValue("experiences[" + i + "].phaseOther", "message.required", "required field.");
                }
                if(!ObjectUtils.isEmpty(exp.getRole()) && Arrays.asList(exp.getRole()).contains(otherString) && StringUtils.isEmpty(exp.getRoleOther())) {
                    errors.rejectValue("experiences[" + i + "].roleOther", "message.required", "required field.");
                }
                if(ObjectUtils.isEmpty(exp.getRole())){
                    errors.rejectValue("experiences[" + i + "].role", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(exp.getWorkingDetails())) {
                    errors.rejectValue("experiences[" + i + "].workingDetails", "message.required", "required field.");
                }
            }
        }
    }

    private boolean isOther(String s) {
        return otherString.equals(s);
    }
}
