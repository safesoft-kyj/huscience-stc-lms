package com.dtnsm.lms.domain.DTO;

import lombok.Data;

import java.io.ByteArrayInputStream;

@Data
public class ReviewHist {
    private String dateOfReview;
    private String cvLabel;

    private String jdLabel;

    private String trLabel;

    private String revName;
    private ByteArrayInputStream revSign;
    private ByteArrayInputStream empSign;
}
