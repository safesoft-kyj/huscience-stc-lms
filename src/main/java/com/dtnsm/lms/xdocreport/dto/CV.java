package com.dtnsm.lms.xdocreport.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class CV implements Serializable {
    private static final long serialVersionUID = -2677710895849019558L;
    private String engName;
    private ByteArrayInputStream sign;
    private String signDate;

    private List<EducationDTO> educations;
    private List<CareerHistoryDTO> careerHistories;
    private List<LicenseDTO> licenses;
    private List<CertificationDTO> certifications;
    private List<MembershipDTO> memberships;
    private List<LanguageDTO> languages;
    private List<ComputerKnowledgeDTO> computerKnowledges;
    private List<ExperienceDTO> experiences;

    public boolean isLicenses() {
        return !ObjectUtils.isEmpty(licenses);
    }

    public boolean isCertification() {
        return !ObjectUtils.isEmpty(certifications);
    }

    public boolean isMemberships() {
        return !ObjectUtils.isEmpty(memberships);
    }

    public boolean isExperiences() {
        return !ObjectUtils.isEmpty(experiences);
    }
}
