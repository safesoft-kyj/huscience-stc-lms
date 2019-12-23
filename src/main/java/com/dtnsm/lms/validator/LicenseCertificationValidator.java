package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.CVCertification;
import com.dtnsm.lms.domain.CVLicense;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LicenseCertificationValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;
        if(!ObjectUtils.isEmpty(cv.getLicenses())) {
            int size = cv.getLicenses().size();
            for(int i = 0; i < size; i ++) {
                CVLicense license = cv.getLicenses().get(i);
                if (StringUtils.isEmpty(license.getNameOfLicense())) {
                    errors.rejectValue("licenses[" + i + "].nameOfLicense", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(license.getLicenseNo())) {
                    errors.rejectValue("licenses[" + i + "].licenseNo", "message.required", "required field.");
                }
                if("Others".equals(license.getLicenseInCountry())) {
                    if(StringUtils.isEmpty(license.getLicenseInCountryOther())) {
                        errors.rejectValue("licenses[" + i + "].licenseInCountryOther", "message.required", "required field.");
                    }
                }
            }
        }

        if(!ObjectUtils.isEmpty(cv.getCertifications())) {
            int size = cv.getCertifications().size();
            for(int i = 0; i < size; i ++) {
                CVCertification cert = cv.getCertifications().get(i);
                if(StringUtils.isEmpty(cert.getNameOfCertification())) {
                    errors.rejectValue("certifications[" + i + "].nameOfCertification", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(cert.getOrganizers())) {
                    errors.rejectValue("certifications[" + i + "].organizers", "message.required", "required field.");
                }
                if(StringUtils.isEmpty(cert.getIssueDate())) {
                    errors.rejectValue("certifications[" + i + "].issueDate", "message.required", "required field.");
                }
            }
        }
    }
}
