package com.dtnsm.lms.component;

import com.dtnsm.lms.domain.constant.ExcelConstant;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ExcelReader {

    public <T> List<T> readFileToList(final MultipartFile multipartFile,
                                      final Function<Row, T> rowFunc) throws IOException, InvalidFormatException {

        final Workbook workbook = readWorkbook(multipartFile);
        final Sheet sheet = workbook.getSheetAt(0);

        int cnt = 0;
        for(int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if(row != null) {
                Cell cell = row.getCell(0); // 문제(A)열
                if(cell != null && cell.getCellType() != CellType.BLANK)
                    cnt++;
            }
        }
        final int rowCount = cnt;

        return IntStream
                .range(0, rowCount)
                .mapToObj(rowIndex -> rowFunc.apply(sheet.getRow(rowIndex)))
                .collect(Collectors.toList());
    }

    private Workbook readWorkbook(MultipartFile multipartFile) throws IOException, InvalidFormatException {
        verifyFileExtension(multipartFile);
        return multipartFileToWorkbook(multipartFile);
    }

    private void verifyFileExtension(MultipartFile multipartFile) throws InvalidFormatException {
        if (!isExcelExtension(multipartFile.getOriginalFilename())) {
            throw new InvalidFormatException("This file extension is not verify");
        }
    }

    private boolean isExcelExtension(String fileName) {
        return fileName.endsWith(ExcelConstant.XLS) || fileName.endsWith(ExcelConstant.XLSX);
    }

    private boolean isExcelXls(String fileName) {
        return fileName.endsWith(ExcelConstant.XLS);
    }

    private boolean isExcelXlsx(String fileName) {
        return fileName.endsWith(ExcelConstant.XLSX);
    }

    private Workbook multipartFileToWorkbook(MultipartFile multipartFile)
            throws IOException {
        if (isExcelXls(multipartFile.getOriginalFilename())) {
            return new HSSFWorkbook(multipartFile.getInputStream());
        } else {
            return new XSSFWorkbook(multipartFile.getInputStream());
        }
    }
}