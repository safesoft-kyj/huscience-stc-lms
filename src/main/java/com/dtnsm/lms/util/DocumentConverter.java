package com.dtnsm.lms.util;

import com.groupdocs.conversion.config.ConversionConfig;
import com.groupdocs.conversion.handler.ConversionHandler;
import com.groupdocs.conversion.handler.ConvertedDocument;
import com.groupdocs.conversion.options.save.MarkupSaveOptions;
import com.groupdocs.conversion.options.save.SaveOptions;
import com.groupdocs.conversion.utils.wrapper.stream.GroupDocsOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Component
@Slf4j
public class DocumentConverter {
    private ConversionConfig config = new ConversionConfig();
    private ConversionHandler conversionHandler;

    @Value("${groupdocs.license}")
    private String license;

    @PostConstruct
    public void init() {
        try {
            com.groupdocs.conversion.License lic = new com.groupdocs.conversion.License();
            lic.setLicense(new FileInputStream(new File(license)));
            log.info("@License Loaded.");
            conversionHandler = new ConversionHandler(config);
        } catch (Exception error) {
            log.error("Error : {}", error.toString());
        }
    }

    public void toHTML(InputStream is, OutputStream os) {
        try {
            SaveOptions saveOption = new MarkupSaveOptions();
            ConvertedDocument convertedDocumentPath = conversionHandler.convert(is, saveOption);
            System.out.print("Converted file path is: " + convertedDocumentPath.getFileType());
            GroupDocsOutputStream outputStream = new GroupDocsOutputStream(os);
            convertedDocumentPath.save(outputStream);
        } catch (Exception error) {
            log.error("error : {}", error);
        } finally {
            if(os != null) {
                try {
                    os.flush();
                    os.close();
                } catch(Exception e) {}
            }
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }
}