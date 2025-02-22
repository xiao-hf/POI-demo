package com.xiao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiao.domain.TpSysUser;
import com.xiao.domain.User;
import com.xiao.mapper.TpSysUserMapper;
import com.xiao.mapper.UserMapper;
import com.xiao.utils.ExcelUtil;
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
        ExcelUtil.writeListToOS(fos, users, TpSysUser.class);
        fos.flush();
        fos.close();
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start) + "ms");
        InputStream fis = Files.newInputStream(Paths.get(filePath));
        List<TpSysUser> listFromExcel = ExcelUtil.getListFromIS(fis, TpSysUser.class);
        fis.close();
        System.out.println(listFromExcel);
    }
}
