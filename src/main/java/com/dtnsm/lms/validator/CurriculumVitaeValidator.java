package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVCareerHistory;
import com.dtnsm.lms.domain.CVEducation;
import com.dtnsm.lms.domain.CVTeamDept;
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
            if(StringUtils.isEmpty(edu.getBachelorsDegree())) {
                errors.rejectValue("educations[" + eduIndex + "].bachelorsDegree", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getMastersDegree())) {
                errors.rejectValue("educations[" + eduIndex + "].mastersDegree", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getMastersThesisTitle())) {
                errors.rejectValue("educations[" + eduIndex + "].mastersThesisTitle", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getMastersName())) {
                errors.rejectValue("educations[" + eduIndex + "].mastersName", "message.required", "required field.");
            }

            if(StringUtils.isEmpty(edu.getPhdDegree())) {
                errors.rejectValue("educations[" + eduIndex + "].phdDegree", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getPhdThesisTitle())) {
                errors.rejectValue("educations[" + eduIndex + "].phdThesisTitle", "message.required", "required field.");
            }
            if(StringUtils.isEmpty(edu.getPhdName())) {
                errors.rejectValue("educations[" + eduIndex + "].phdName", "message.required", "required field.");
            }

            eduIndex ++;
        }

        int historyIndex = 0;
//        int historySize = cv.getCareerHistories().size();
        for(CVCareerHistory history : cv.getCareerHistories()) {
            //첫번째 row만 present 선택 가능
            if(history.isPresent() && historyIndex > 0) {
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
            for(int x = 0; x < history.getCvTeamDepts().size(); x ++ ) {
                CVTeamDept cvTeamDept = history.getCvTeamDepts().get(x);
                if (StringUtils.isEmpty(cvTeamDept.getPosition())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].position", "message.required", "required field.");
                }
                if (StringUtils.isEmpty(cvTeamDept.getTeam())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].team", "message.required", "required field.");
                }
                if (StringUtils.isEmpty(cvTeamDept.getDepartment())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].department", "message.required", "required field.");
                }
            }

            historyIndex ++;
        }
    }
}
