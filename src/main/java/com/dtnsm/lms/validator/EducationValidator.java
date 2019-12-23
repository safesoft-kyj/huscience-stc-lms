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

            if("Others".equals(edu.getNameOfUniversity())) {
                if(StringUtils.isEmpty(edu.getNameOfUniversityOther())) {
                    errors.rejectValue("educations[" + eduIndex + "].nameOfUniversityOther", "message.required", "required field.");
                }
            }
            if("Others".equals(edu.getCityCountry())) {
                if(StringUtils.isEmpty(edu.getCityCountryOther())) {
                    errors.rejectValue("educations[" + eduIndex + "].cityCountryOther", "message.required", "required field.");
                }
            }
            if(StringUtils.isEmpty(edu.getBachelorsDegree())) {
                errors.rejectValue("educations[" + eduIndex + "].bachelorsDegree", "message.required", "required field.");
            }

            if(!StringUtils.isEmpty(edu.getMastersThesisTitle()) || !StringUtils.isEmpty(edu.getMastersName())) {
                if(StringUtils.isEmpty(edu.getMastersDegree())) {
                    errors.rejectValue("educations[" + eduIndex + "].mastersDegree", "message.required", "required field.");
                }
            }

            if(!StringUtils.isEmpty(edu.getMastersDegree())) {
//                errors.rejectValue("educations[" + eduIndex + "].mastersDegree", "message.required", "required field.");

                if(StringUtils.isEmpty(edu.getMastersThesisTitle())) {
                    errors.rejectValue("educations[" + eduIndex + "].mastersThesisTitle", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(edu.getMastersName())) {
                    errors.rejectValue("educations[" + eduIndex + "].mastersName", "message.required", "required field.");
                }
            } else {
                if(!StringUtils.isEmpty(edu.getMastersThesisTitle()) || !StringUtils.isEmpty(edu.getMastersName())) {
                    errors.rejectValue("educations[" + eduIndex + "].mastersDegree", "message.required", "required field.");
                }
            }



            if(!StringUtils.isEmpty(edu.getPhdDegree())) {
                if(StringUtils.isEmpty(edu.getMastersDegree())) {
                    errors.rejectValue("educations[" + eduIndex + "].mastersDegree", "message.required", "required field.");
                }
//                errors.rejectValue("educations[" + eduIndex + "].phdDegree", "message.required", "required field.");
                if(StringUtils.isEmpty(edu.getPhdThesisTitle())) {
                    errors.rejectValue("educations[" + eduIndex + "].phdThesisTitle", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(edu.getPhdName())) {
                    errors.rejectValue("educations[" + eduIndex + "].phdName", "message.required", "required field.");
                }
            } else {
                if(!StringUtils.isEmpty(edu.getPhdThesisTitle()) || !StringUtils.isEmpty(edu.getPhdName())) {
                    errors.rejectValue("educations[" + eduIndex + "].phdDegree", "message.required", "required field.");
                }
            }




            eduIndex ++;
        }
    }
}
