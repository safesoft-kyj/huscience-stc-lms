package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVMembership;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MembershipValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;

        if(!ObjectUtils.isEmpty(cv.getMemberships())) {
            int size = cv.getMemberships().size();
            for(int i = 0; i < size; i ++) {
                CVMembership mem = cv.getMemberships().get(i);
                if(StringUtils.isEmpty(mem.getMembershipName())) {
                    errors.rejectValue("memberships[" + i + "].membershipName", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(mem.getStartYear())) {
                    errors.rejectValue("memberships[" + i + "].startYear", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(mem.getEndYear()) && !mem.isPresent()) {
                    errors.rejectValue("memberships[" + i + "].endYear", "message.required", "required field.");
                }
            }
        }
    }
}
