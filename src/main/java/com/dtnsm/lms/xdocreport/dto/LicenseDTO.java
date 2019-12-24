package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LicenseDTO {
    private String licenseName;
    private String licenseNo;
    private String licenseInCountry;

    @Builder
    public LicenseDTO(String licenseName, String licenseNo, String licenseInCountry) {
        this.licenseName = licenseName;
        this.licenseNo = licenseNo;
        this.licenseInCountry = licenseInCountry;
    }
}
