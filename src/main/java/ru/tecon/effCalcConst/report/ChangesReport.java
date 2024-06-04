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
        row_2.setHeight((short) 435);
        SXSSFCell cell_2_1 = row_2.createCell(0);
        cell_2_1.setCellValue("Автоматизированный расчет эффекта");
        CellRangeAddress formName = new CellRangeAddress(1, 1, 0, 7);
        sh.addMergedRegion(formName);
        cell_2_1.setCellStyle(headerStyle);

        SXSSFRow row_3 = sh.createRow(2);
        row_3.setHeight((short) 435);
        SXSSFCell cell_3_1 = row_3.createCell(0);
        cell_3_1.setCellValue("Для " + bean.getObjName(obj_id));
        CellRangeAddress objName = new CellRangeAddress(2, 2, 0, 7);
        sh.addMergedRegion(objName);
        cell_3_1.setCellStyle(headerStyle);

        if (!tableData.isEmpty()) {
            SXSSFRow row_4 = sh.createRow(3);
            row_4.setHeight((short) 435);
            SXSSFCell cell_4_1 = row_4.createCell(0);
            if (id != 0) {
                sh.setColumnWidth(0, 16 * 256);
                sh.setColumnWidth(1, 16 * 256);
                sh.setColumnWidth(2, 17 * 256);
                cell_4_1.setCellValue("История изменений параметра: " + tableData.get(0).getConstName());
                cell_4_1.setCellStyle(headerNameStyle);
                CellRangeAddress repName = new CellRangeAddress(3, 3, 0, 7);
                sh.addMergedRegion(repName);
                cell_4_1.setCellStyle(headerStyle);
            } else {
                sh.setColumnWidth(0, 35 * 256);
                sh.setColumnWidth(1, 15 * 256);
                sh.setColumnWidth(2, 15 * 256);
                sh.setColumnWidth(3, 16 * 256);
                sh.setColumnWidth(4, 19 * 256);

                cell_4_1.setCellValue("Общая история изменений параметров");
                CellRangeAddress repName = new CellRangeAddress(3, 3, 0, 7);
                sh.addMergedRegion(repName);
                cell_4_1.setCellStyle(headerStyle);
            }

            createNow(sh, nowStyle);

            if (id !=0) {
                SXSSFRow row_7 = sh.createRow(6);
                SXSSFCell cell_7_1 = row_7.createCell(0);
                cell_7_1.setCellStyle(tableHeaderStyle);
                cell_7_1.setCellValue("Дата и время");

                SXSSFCell cell_7_2 = row_7.createCell(1);
                cell_7_2.setCellStyle(tableHeaderStyle);
                cell_7_2.setCellValue("Обновленное значение");

                SXSSFCell cell_7_3 = row_7.createCell(2);
                cell_7_3.setCellStyle(tableHeaderStyle);
                cell_7_3.setCellValue("Имя пользователя");

                int i = 0;
                for (ParamHistory paramHistory : tableData) {
                    SXSSFRow row = sh.createRow(7 + i);

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
                SXSSFRow row_7 = sh.createRow(6);

                SXSSFCell cell_7_1 = row_7.createCell(0);
                cell_7_1.setCellStyle(tableHeaderStyle);
                cell_7_1.setCellValue("Наименование показателя");

                SXSSFCell cell_7_2 = row_7.createCell(1);
                cell_7_2.setCellStyle(tableHeaderStyle);
                cell_7_2.setCellValue("Старое");

                SXSSFCell cell_7_3 = row_7.createCell(2);
                cell_7_3.setCellStyle(tableHeaderStyle);
                cell_7_3.setCellValue("Новое");

                SXSSFCell cell_7_4 = row_7.createCell(3);
                cell_7_4.setCellStyle(tableHeaderStyle);
                cell_7_4.setCellValue("Дата и время");

                SXSSFCell cell_7_5 = row_7.createCell(4);
                cell_7_5.setCellStyle(tableHeaderStyle);
                cell_7_5.setCellValue("Имя пользователя");

                int i = 0;
                for (ParamHistory paramHistory : tableData) {
                    SXSSFRow row = sh.createRow(7 + i);

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
                cell_4_1.setCellValue("История изменений параметра: Отсутствует");
                cell_4_1.setCellStyle(headerNameStyle);

            createNow(sh, nowStyle);
        }

        return wb;
    }

    private static void createNow(SXSSFSheet sh, CellStyle nowStyle) {
        String now = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        SXSSFRow row_5 = sh.createRow(4);
        row_5.setHeight((short) 435);
        SXSSFCell cell_5_1 = row_5.createCell(0);
        cell_5_1.setCellStyle(nowStyle);
        cell_5_1.setCellValue("Отчет сформирован  " + now);
        CellRangeAddress nowDone = new CellRangeAddress(4, 4, 0, 7);
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
