package ru.tecon.effCalcConst.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.model.ParamHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChangesReport {

    public static SXSSFWorkbook createReport(int id, int obj_id, EffCalcConstSB bean) {
        List<ParamHistory> tableData = bean.getParamHistory(id, obj_id);
        SXSSFWorkbook wb = new SXSSFWorkbook();
        SXSSFSheet sh = wb.createSheet("Отчет");

        CellStyle headerStyle = setHeaderStyle(wb);
        CellStyle headerStyleNoBold = setHeaderStyleNoBold(wb);
        CellStyle longHeaderStyle = setLongHeaderStyle(wb);
        CellStyle headerNameStyle = setHeaderNameStyle(wb);
        CellStyle nowStyle = setCellNow (wb);
        CellStyle tableHeaderStyle = setTableHeaderStyle(wb);
        CellStyle cellNoWrapStyle = setCellNoWrapStyle(wb);
        CellStyle cellWrapStyle = setCellWrapStyle(wb);

        SXSSFRow row_1 = sh.createRow(0);
        row_1.setHeight((short) 435);
        SXSSFCell cell_1_1 = row_1.createCell(0);
        cell_1_1.setCellValue("ПАО \"МОЭК\": АС \"ТЕКОН - Диспетчеризация\"");
        CellRangeAddress title = new CellRangeAddress(0, 0, 0, 7);
        sh.addMergedRegion(title);
        cell_1_1.setCellStyle(headerStyle);

        SXSSFRow row_2 = sh.createRow(1);
        SXSSFCell cell_2_1 = row_2.createCell(0);
        if (id != 0) {
            cell_2_1.setCellValue("Изменения нормативного значения: " + bean.getConstName(id));
            sh.setColumnWidth(0, 16 * 256);
            sh.setColumnWidth(1, 16 * 256);
            sh.setColumnWidth(2, 17 * 256);
            sh.setColumnWidth(3, 17 * 256);
            sh.setColumnWidth(4, 17 * 256);
            sh.setColumnWidth(5, 17 * 256);
            sh.setColumnWidth(6, 17 * 256);
            sh.setColumnWidth(7, 17 * 256);
            if (id == 15 || id == 16 || id == 17 || id == 18 || id == 21) {
                row_2.setHeight((short) 870);
                cell_2_1.setCellStyle(longHeaderStyle);
            } else {
                row_2.setHeight((short) 435);
                cell_2_1.setCellStyle(headerStyle);
            }
        } else {
            cell_2_1.setCellValue("Изменения нормативных значений");
            sh.setColumnWidth(0, 35 * 256);
            sh.setColumnWidth(1, 15 * 256);
            sh.setColumnWidth(2, 15 * 256);
            sh.setColumnWidth(3, 16 * 256);
            sh.setColumnWidth(4, 19 * 256);
            row_2.setHeight((short) 435);
            cell_2_1.setCellStyle(headerStyle);
        }
        CellRangeAddress formName = new CellRangeAddress(1, 1, 0, 7);
        sh.addMergedRegion(formName);

        SXSSFRow row_3 = sh.createRow(2);
        row_3.setHeight((short) 435);
        SXSSFCell cell_3_1 = row_3.createCell(0);

        if (obj_id == 545) {
            cell_3_1.setCellValue("По " + bean.getStruct(obj_id));
        }else {
            cell_3_1.setCellValue(bean.getStruct(obj_id));
        }
        CellRangeAddress objName = new CellRangeAddress(2, 2, 0, 7);
        sh.addMergedRegion(objName);
        cell_3_1.setCellStyle(headerStyleNoBold);

        if (!tableData.isEmpty()) {
            SXSSFRow row_4 = sh.createRow(3);
            row_4.setHeight((short) 435);

            SXSSFRow row_5 = sh.createRow(4);
            row_5.setHeight((short) 435);

            createNow(sh, nowStyle);

            if (id !=0) {
                SXSSFRow row_6 = sh.createRow(7);
                SXSSFCell cell_6_1 = row_6.createCell(0);
                cell_6_1.setCellStyle(tableHeaderStyle);
                cell_6_1.setCellValue("Дата и время");

                SXSSFCell cell_6_2 = row_6.createCell(1);
                cell_6_2.setCellStyle(tableHeaderStyle);
                cell_6_2.setCellValue("Обновленное значение");

                SXSSFCell cell_6_3 = row_6.createCell(2);
                cell_6_3.setCellStyle(tableHeaderStyle);
                cell_6_3.setCellValue("Имя пользователя");

                int i = 0;
                for (ParamHistory paramHistory : tableData) {
                    SXSSFRow row = sh.createRow(8 + i);

                    SXSSFCell cell_i_1 = row.createCell(0);
                    cell_i_1.setCellStyle(cellNoWrapStyle);
                    cell_i_1.setCellValue(paramHistory.getDate());

                    SXSSFCell cell_i_2 = row.createCell(1);
                    cell_i_2.setCellStyle(cellNoWrapStyle);
                    cell_i_2.setCellValue(paramHistory.getNewValue());

                    SXSSFCell cell_i_3 = row.createCell(2);
                    cell_i_3.setCellStyle(cellWrapStyle);
                    cell_i_3.setCellValue(paramHistory.getUserName());

                    i++;
                }
            } else {
                SXSSFRow row_6 = sh.createRow(7);

                SXSSFCell cell_6_1 = row_6.createCell(0);
                cell_6_1.setCellStyle(tableHeaderStyle);
                cell_6_1.setCellValue("Наименование показателя");

                SXSSFCell cell_6_2 = row_6.createCell(1);
                cell_6_2.setCellStyle(tableHeaderStyle);
                cell_6_2.setCellValue("Старое");

                SXSSFCell cell_6_3 = row_6.createCell(2);
                cell_6_3.setCellStyle(tableHeaderStyle);
                cell_6_3.setCellValue("Новое");

                SXSSFCell cell_6_4 = row_6.createCell(3);
                cell_6_4.setCellStyle(tableHeaderStyle);
                cell_6_4.setCellValue("Дата и время");

                SXSSFCell cell_6_5 = row_6.createCell(4);
                cell_6_5.setCellStyle(tableHeaderStyle);
                cell_6_5.setCellValue("Имя пользователя");

                int i = 0;
                for (ParamHistory paramHistory : tableData) {
                    SXSSFRow row = sh.createRow(8 + i);

                    SXSSFCell cell_i_1 = row.createCell(0);
                    cell_i_1.setCellStyle(cellWrapStyle);
                    cell_i_1.setCellValue(paramHistory.getConstName());

                    SXSSFCell cell_i_2 = row.createCell(1);
                    cell_i_2.setCellStyle(cellNoWrapStyle);
                    cell_i_2.setCellValue(paramHistory.getOldValue());

                    SXSSFCell cell_i_3 = row.createCell(2);
                    cell_i_3.setCellStyle(cellNoWrapStyle);
                    cell_i_3.setCellValue(paramHistory.getNewValue());

                    SXSSFCell cell_i_4 = row.createCell(3);
                    cell_i_4.setCellStyle(cellNoWrapStyle);
                    cell_i_4.setCellValue(paramHistory.getDate());

                    SXSSFCell cell_i_5 = row.createCell(4);
                    cell_i_5.setCellStyle(cellWrapStyle);
                    cell_i_5.setCellValue(paramHistory.getUserName());

                    i++;
                }
            }
        } else {
            SXSSFRow row_4 = sh.createRow(3);
            row_4.setHeight((short) 435);
            SXSSFCell cell_4_1 = row_3.createCell(0);
                sh.setColumnWidth(0, 16 * 256);
                sh.setColumnWidth(1, 16 * 256);
                sh.setColumnWidth(2, 17 * 256);
                cell_4_1.setCellValue("Изменения нормативных значений отсутствуют");
                cell_4_1.setCellStyle(headerNameStyle);

            createNow(sh, nowStyle);
        }

        return wb;
    }

    private static void createNow(SXSSFSheet sh, CellStyle nowStyle) {
        String now = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        SXSSFRow row_5 = sh.createRow(5);
        row_5.setHeight((short) 435);
        SXSSFCell cell_5_1 = row_5.createCell(0);
        cell_5_1.setCellStyle(nowStyle);
        cell_5_1.setCellValue("Отчет сформирован  " + now);
        CellRangeAddress nowFirst = new CellRangeAddress(3, 3, 0, 7);
        CellRangeAddress nowSecond = new CellRangeAddress(4, 4, 0, 7);
        CellRangeAddress nowDone = new CellRangeAddress(5, 5, 0, 7);

        sh.addMergedRegion(nowFirst);
        sh.addMergedRegion(nowSecond);
        sh.addMergedRegion(nowDone);
    }

    ///////////////////////////////////////////  Определение стилей тут

    //  Стиль заголовка жирный
    private static CellStyle setHeaderStyle(SXSSFWorkbook p_wb) {

        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(false);

        Font headerFont = p_wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 16);

        style.setFont(headerFont);

        return style;
    }

    //  Стиль заголовка жирный с переносом строк
    private static CellStyle setLongHeaderStyle(SXSSFWorkbook p_wb) {

        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);

        Font headerFont = p_wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 16);

        style.setFont(headerFont);

        return style;
    }

    //  Стиль заголовка не жирный
    private static CellStyle setHeaderStyleNoBold(SXSSFWorkbook p_wb) {

        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(false);

        Font headerFont = p_wb.createFont();
        headerFont.setBold(false);
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 16);

        style.setFont(headerFont);

        return style;
    }

    //стиль для имени отчета

    private static CellStyle setHeaderNameStyle(SXSSFWorkbook p_wb) {

        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(false);

        Font headerFont = p_wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 16);

        style.setFont(headerFont);

        return style;
    }

    //стиль для даты создания отчета
    private static CellStyle setCellNow(SXSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);


        Font nowFont = wb.createFont();
        nowFont.setBold(false);
        nowFont.setFontName("Times New Roman");
        nowFont.setFontHeightInPoints((short) 12);

        style.setFont(nowFont);

        return style;
    }

    //  Стиль шапки таблицы
    private static CellStyle setTableHeaderStyle(SXSSFWorkbook p_wb) {
        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        style.setBorderTop(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THICK);

        Font tableHeaderFont = p_wb.createFont();

        tableHeaderFont.setBold(true);
        tableHeaderFont.setFontName("Times New Roman");
        tableHeaderFont.setFontHeightInPoints((short) 12);

        style.setFont(tableHeaderFont);

        return style;
    }

    private static CellStyle setCellNoWrapStyle(SXSSFWorkbook p_wb) {
        CellStyle style = setCellTemplate(p_wb);
        style.setWrapText(false);
        return style;
    }

    private static CellStyle setCellWrapStyle(SXSSFWorkbook p_wb) {
        CellStyle style = setCellTemplate(p_wb);
        style.setWrapText(true);
        return style;
    }

    private static CellStyle setCellTemplate(SXSSFWorkbook p_wb) {
        CellStyle style = p_wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        Font cellNoBoldFont = p_wb.createFont();

        cellNoBoldFont.setBold(false);
        cellNoBoldFont.setFontName("Times New Roman");
        cellNoBoldFont.setFontHeightInPoints((short) 11);

        style.setFont(cellNoBoldFont);

        return style;
    }
}
