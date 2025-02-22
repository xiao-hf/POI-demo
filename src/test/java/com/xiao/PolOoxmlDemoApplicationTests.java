package com.xiao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiao.domain.TpSysUser;
import com.xiao.domain.User;
import com.xiao.mapper.TpSysUserMapper;
import com.xiao.mapper.UserMapper;
import com.xiao.utils.PoiUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest
class PolOoxmlDemoApplicationTests {

    @Resource
    UserMapper userMapper;

    @Test
    void test() throws NoSuchFieldException {
        Class<User> clazz = User.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
            System.out.println(field.getName());
        String id = clazz.getDeclaredField("username").getName();
        System.out.println(id);
    }

    @Resource
    TpSysUserMapper tpSysUserMapper;

    @Test
    void main() throws Exception {
        //String filePath = "D:\\bak\\mine.xlsx";
        String filePath = "D:\\bak\\mine.xlsx";
        List<TpSysUser> users = tpSysUserMapper.selectOrderByExpireTimeDesc();
        long start = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(filePath);
        PoiUtil.writeListToOS(fos, users, TpSysUser.class);
        fos.flush();
        fos.close();
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start) + "ms");
        InputStream fis = Files.newInputStream(Paths.get(filePath));
        List<TpSysUser> listFromExcel = PoiUtil.getListFromIS(fis, TpSysUser.class);
        fis.close();
        System.out.println(listFromExcel);
    }

    @Test
    public <T> List<T> getListFromExcel(String filePath, Class<T> clazz) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheetAt(0);
        List<T> res = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fs = new ArrayList<>();
        int len = 0;
        for (Field field : fields) {
            TableField tableField = field.getAnnotation(TableField.class);
            len += tableField == null ? 1 : 0;
        }
        HSSFRow row = sheet.getRow(0);
        for (int j = 0; j < len; j++) {
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
                if (c >= 'a' && c <= 'z')
                    c += 'A' - 'a';
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
    }

    @Test
    public <T> void saveListToExcel(String filePath, List<T> list, Class<T> clazz) throws Exception {
        FileOutputStream fos = new FileOutputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        HSSFRow row = sheet.createRow(0);
        for (int j = 0; j < fields.length; j++) {
            TableField flag = fields[j].getAnnotation(TableField.class);
            if (flag != null)
                continue;
            HSSFCell cell = row.createCell(j);
            cell.setCellValue(fields[j].getName());
        }
        for (int i = 1; i <= list.size(); i++) {
            T t = list.get(i - 1);
            row = sheet.createRow(i);
            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                TableField flag = field.getAnnotation(TableField.class);
                if (flag != null)
                    continue;
                String name = field.getName();
                char c = name.charAt(0);
                if (c >= 'a' && c <= 'z')
                    c += 'A' - 'a';
                name = "get" + c + name.substring(1);
                Method method = clazz.getMethod(name);
                Object val = method.invoke(t);
                setVal(workbook, row.createCell(j), val);
            }
        }
        workbook.write(fos);
        workbook.close();
        fos.close();
    }

    @Test
    public void setVal(HSSFWorkbook workbook, HSSFCell cell, Object val) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFCreationHelper creationHelper = workbook.getCreationHelper();
        if (val instanceof Date) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue((Date) val);
        } else if (val instanceof LocalDateTime) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue((LocalDateTime) val);
        } else if (val instanceof Calendar) {
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue((Calendar) val);
        } else if (val instanceof Integer) {
            cell.setCellValue((int) val);
        } else if (val instanceof Long) {
            cell.setCellValue((long) val);
        } else if (val instanceof Double) {
            cell.setCellValue((double) val);
        } else {
            cell.setCellValue(val.toString());
        }
    }

    @Test
    public void createFile(String filePath, String ... sheets) throws Exception {
        FileOutputStream fos = new FileOutputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (String sheet : sheets)
            workbook.createSheet(sheet);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }

}
