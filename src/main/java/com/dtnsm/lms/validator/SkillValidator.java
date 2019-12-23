package com.dtnsm.lms.validator;

import com.dtnsm.lms.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SkillValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CurriculumVitae cv = (CurriculumVitae)o;

        if(!ObjectUtils.isEmpty(cv.getLanguages())) {
            int size = cv.getLanguages().size();
            for(int i = 0; i < size; i ++) {
                CVLanguage lang = cv.getLanguages().get(i);
                if("Others".equals(lang.getLanguage()) && StringUtils.isEmpty(lang.getLanguageOther())) {
                    errors.rejectValue("languages[" + i + "].languageOther", "message.required", "required field.");
                }

                if(!ObjectUtils.isEmpty(lang.getLanguageCertifications())) {
                    int cSize = lang.getLanguageCertifications().size();
                    for(int x = 0; x < cSize; x ++) {
                        CVLanguageCertification langCert = lang.getLanguageCertifications().get(x);
                        if("Others".equals(langCert.getCertificateProgram()) && StringUtils.isEmpty(langCert.getCertificateProgramOther())) {
                            errors.rejectValue("languages[" + i + "].languageCertifications["+x+"].certificateProgramOther", "message.required", "required field.");
                        }

                        if(StringUtils.isEmpty(langCert.getLevelOrScore())) {
                            errors.rejectValue("languages[" + i + "].languageCertifications["+x+"].levelOrScore", "message.required", "required field.");
                        }
                    }
                }
            }
        }

        if(!ObjectUtils.isEmpty(cv.getComputerKnowledges())) {
            int size = cv.getComputerKnowledges().size();
            for(int i = 0; i < size; i ++) {
                CVComputerKnowledge computerKnowledge = cv.getComputerKnowledges().get(i);

                if(StringUtils.isEmpty(computerKnowledge.getProgramName())) {
                    errors.rejectValue("computerKnowledges[" + i + "].programName", "message.required", "required field.");
                }

                if(!ObjectUtils.isEmpty(computerKnowledge.getComputerCertifications())) {
                    int cSize = computerKnowledge.getComputerCertifications().size();

                    for(int x = 0; x < cSize; x ++) {
                        CVComputerCertification computerCertification = computerKnowledge.getComputerCertifications().get(x);
                        if(StringUtils.isEmpty(computerCertification.getCertificateProgram())) {
                            errors.rejectValue("computerKnowledges[" + i + "].computerCertifications["+x+"].certificateProgram", "message.required", "required field.");
                        }
                    }
                }
            }
        }
    }
}
