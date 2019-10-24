package com.dtnsm.lms.controller;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.Product;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("upload")
public class UploadExcelController {

    @Autowired
    private ExcelReader excelReader;

    @PostMapping("excel")
    public List<Product> readExcel(@RequestParam("file") MultipartFile multipartFile) throws IOException, InvalidFormatException {
        return excelReader.readFileToList(multipartFile, Product::from);
    }
}