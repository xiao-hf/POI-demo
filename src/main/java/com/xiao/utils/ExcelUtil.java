package com.xiao.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    public static <T> void writeListToOS(OutputStream fos, List<T> list, Class<T> clazz) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            HSSFSheet sheet = workbook.createSheet(clazz.getSimpleName());
            Field[] fields = clazz.getDeclaredFields();
            HSSFRow row = sheet.createRow(0);
            int[] maxWidths = new int[fields.length];
            for (int j = 0; j < fields.length; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(headerStyle);
                String fieldName = fields[j].getName();
                cell.setCellValue(fieldName);
                maxWidths[j] = fieldName.getBytes().length * 256 + 512;
            }
            for (int i = 1; i <= list.size(); i++) {
                T t = list.get(i - 1);
                row = sheet.createRow(i);
                for (int j = 0; j < fields.length; j++) {
                    Field field = fields[j];
                    String name = field.getName();
                    char c = name.charAt(0);
                    c += c >= 'a' && c <= 'z' ? 'A' - 'a' : 0;
                    name = "get" + c + name.substring(1);
                    Method method = clazz.getMethod(name);
                    Object val = method.invoke(t);
                    HSSFCell cell = row.createCell(j);
                    setVal(workbook, cell, val);
                    if (val != null)
                        maxWidths[j] = Math.max(maxWidths[j], val.toString().getBytes().length * 256 + 512);
                }
            }
            for (int i = 0; i < fields.length; i++)
                sheet.setColumnWidth(i, Math.max(Math.min(maxWidths[i], 255 * 256), 10 * 256));
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getListFromIS(InputStream fis, Class<T> clazz) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0);
            List<T> res = new ArrayList<>();
            Field[] fields = clazz.getDeclaredFields();
            List<Field> fs = new ArrayList<>();
            int len = fields.length;
            HSSFRow row = sheet.getRow(0);
            for (int j = 0; j < len; j++) {
                sheet.setColumnWidth(j, 15 * 256);
                fs.add(clazz.getDeclaredField(row.getCell(j).getStringCellValue()));
            }
            int mx = sheet.getLastRowNum();
            for (int i = 1; i <= mx; i++) {
                T t = clazz.newInstance();
                row = sheet.getRow(i);
                for (int j = 0; j < len; j++) {
                    Field field = fs.get(j);
                    Class<?> fieldType = field.getType();
                    HSSFCell cell = row.getCell(j);
                    String fieldName = fields[j].getName();
                    char c = fieldName.charAt(0);
                    c += c >= 'a' && c <= 'z' ? 'A' - 'a' : 0;
                    String methodName = "set" + c + fieldName.substring(1);
                    Method method = clazz.getMethod(methodName, fieldType);
                    if (fieldType == Integer.class || fieldType == int.class)
                        method.invoke(t, (int) cell.getNumericCellValue());
                    else if (fieldType == Long.class || fieldType == long.class)
                        method.invoke(t, (long) cell.getNumericCellValue());
                    else if (fieldType == Double.class || fieldType == double.class)
                        method.invoke(t, cell.getNumericCellValue());
                    else if (fieldType == Date.class)
                        method.invoke(t, cell.getDateCellValue());
                    else if (fieldType == Boolean.class)
                        method.invoke(t, cell.getBooleanCellValue());
                    else
                        method.invoke(t, cell.getStringCellValue());
                }
                res.add(t);
            }
            workbook.close();
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile(String filePath, String ... sheets) throws Exception {
        FileOutputStream fos = new FileOutputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (String sheet : sheets)
            workbook.createSheet(sheet);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }

    private static void setVal(HSSFWorkbook workbook, HSSFCell cell, Object val) {
        CellStyle cellStyle = getDataStyle(workbook);
        cell.setCellStyle(cellStyle);
        if (val == null)
            return;
        HSSFCreationHelper creationHelper = workbook.getCreationHelper();
        if (val instanceof Date) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellValue((Date) val);
        } else if (val instanceof LocalDateTime) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellValue((LocalDateTime) val);
        } else if (val instanceof Calendar) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
            cell.setCellValue((Calendar) val);
        } else if (val instanceof Integer) {
            cell.setCellValue((int) val);
        } else if (val instanceof Long) {
            cell.setCellValue((long) val);
        } else if (val instanceof Double) {
            cell.setCellValue((double) val);
        } else if (val instanceof Boolean) {
            cell.setCellValue((boolean) val);
        } else {
            cell.setCellValue(val.toString());
        }
    }

    private static CellStyle getDataStyle(HSSFWorkbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return dataStyle;
    }
}
