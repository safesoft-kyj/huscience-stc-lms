package com.dtnsm.lms.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
public abstract class DocumentLicense {


    public void license() {
        try {

        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
        }
    }

    public abstract void init();
}
