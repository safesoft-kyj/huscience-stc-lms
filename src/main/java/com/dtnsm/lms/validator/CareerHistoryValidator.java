package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVCareerHistory;
import com.dtnsm.lms.domain.CVTeamDept;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CareerHistoryValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae) o;

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
            if("Others".equals(history.getCityCountry())) {
                if(StringUtils.isEmpty(history.getCityCountryOther())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].cityCountryOther", "message.required", "required field.");
                }
            }
            for(int x = 0; x < history.getCvTeamDepts().size(); x ++ ) {
                CVTeamDept cvTeamDept = history.getCvTeamDepts().get(x);
                if (StringUtils.isEmpty(cvTeamDept.getPosition())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].cvTeamDepts["+x+"].position", "message.required", "required field.");
                }
                if (StringUtils.isEmpty(cvTeamDept.getTeam())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].cvTeamDepts["+x+"].team", "message.required", "required field.");
                }
                if (StringUtils.isEmpty(cvTeamDept.getDepartment())) {
                    errors.rejectValue("careerHistories[" + historyIndex + "].cvTeamDepts["+x+"].department", "message.required", "required field.");
                }
            }

            historyIndex ++;
        }
    }
}