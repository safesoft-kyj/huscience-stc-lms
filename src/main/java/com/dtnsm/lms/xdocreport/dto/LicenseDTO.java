package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LicenseDTO {
    private String licenseNo;
    private String licenseInCountry;

    @Builder
    public LicenseDTO(String licenseNo, String licenseInCountry) {
        this.licenseNo = licenseNo;
        this.licenseInCountry = licenseInCountry;
    }
}
