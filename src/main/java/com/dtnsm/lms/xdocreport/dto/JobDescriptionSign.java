package com.dtnsm.lms.xdocreport.dto;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import lombok.Builder;
import lombok.Data;

@Data
public class JobDescriptionSign {
    private String assignDate;

    private String employeeName;
    private String agreeDate;

    private String managerName;
    private String managerTitle;
    private String approvedDate;

    private String empSignStr;
    private IImageProvider empSign;

    private String mngSignStr;
    private IImageProvider mngSign;

    @Builder
    public JobDescriptionSign(String assignDate, String employeeName, String agreeDate, String managerName, String managerTitle, String approvedDate,
                              IImageProvider empSign, IImageProvider mngSign, String empSignStr, String mngSignStr) {
        this.assignDate = assignDate;
        this.employeeName = employeeName;
        this.agreeDate = agreeDate;
        this.managerName = managerName;
        this.managerTitle = managerTitle;
        this.approvedDate = approvedDate;
        this.empSign = empSign;
        this.mngSign = mngSign;
        this.empSignStr = empSignStr;
        this.mngSignStr = mngSignStr;
    }
}
