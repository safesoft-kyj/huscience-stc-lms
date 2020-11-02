package com.dtnsm.lms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public  class ExcelUtil {

    // 문자열 날짜에 하루 더하기
    public static String getStringByCellValue(Cell cell) {

        DataFormatter formatter = new DataFormatter();

        String cellString = "";

        log.info("Cell Type: {}, Cell Text: {}", cell.getCellType(), cell.toString());

        if(cell.getCellType() == CellType.STRING) {
            cellString = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
                cellString = cell.toString();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isValidExcelDate(cell.getNumericCellValue()) && cell.getNumericCellValue() > 100) {
                Date date = cell.getDateCellValue();

                cellString = new SimpleDateFormat("yyyy년 MM월 dd일").format(date);
            } else {
                //cellString = String.valueOf(cell.getNumericCellValue());

                cellString = String.valueOf(Math.round(cell.getNumericCellValue()));
//                cellString = formatter.formatCellValue(cell);
            }
        } else {
            cellString = "변환오류";
        }
//        } else {
//            CellValue evaluate = formulaEvaluator.evaluate(cell);
//            if(evaluate != null) {
//                cellString = evaluate.formatAsString();
//            }
//        }
        return cellString;
    }

    public static boolean isInternalDateFormat(int format) {
        switch(format) {
            // Internal Date Formats as described on page 427 in
            // Microsoft Excel Dev's Kit...
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x2d:
            case 0x2e:
            case 0x2f:
                return true;
        }
        return false;
    }
}
