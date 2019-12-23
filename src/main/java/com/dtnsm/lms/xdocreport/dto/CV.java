package com.dtnsm.lms.xdocreport.dto;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class CV implements Serializable {
    private static final long serialVersionUID = -2677710895849019558L;
    private String engName;
    private IImageProvider sign;
    private String signDate;

    private List<EducationDTO> educations;
    private List<CareerHistoryDTO> careerHistories;
    private List<LicenseDTO> licenses;
    private List<CertificationDTO> certifications;
    private List<MembershipDTO> memberships;
    private List<LanguageDTO> languages;
    private List<ComputerKnowledgeDTO> computerKnowledges;
    private List<ExperienceDTO> experiences;
}
