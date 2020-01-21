package com.dtnsm.lms.test;

import com.groupdocs.merger.Merger;

public class MergeTest {
    public static void main(String[] args) throws Exception {
        com.groupdocs.merger.licensing.License mergerLicense = new com.groupdocs.merger.licensing.License();
        mergerLicense.setLicense("C:\\dev\\license\\GroupDocs.Total.Java.lic");

        String path = "C:\\Users\\JHSEO\\Downloads\\test\\";
        Merger merger = new Merger(path + "mjlee_cover_2.pdf");
        merger.join(path + "mjlee_CV_7.pdf");
        merger.join(path + "1579590086139_mjlee_tm.pdf");
        merger.join(path + "1579590086139_mjlee_certi.pdf");

        merger.save(path + "output.pdf");
    }
}
