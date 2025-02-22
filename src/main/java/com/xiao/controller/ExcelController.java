package com.xiao.controller;

import com.xiao.domain.TpSysUser;
import com.xiao.mapper.TpSysUserMapper;
import com.xiao.utils.PoiUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("excel")
public class ExcelController {
    @Resource
    TpSysUserMapper tpSysUserMapper;
    @GetMapping("export")
    public String export(HttpServletResponse resp) throws IOException {
        List<TpSysUser> users = tpSysUserMapper.selectOrderByExpireTimeDesc();
        ServletOutputStream os = resp.getOutputStream();
        String fileName = "users.xlsx";
        resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        PoiUtil.writeListToOS(os, users, TpSysUser.class);
        os.flush();
        os.close();
        return "导出成功!";
    }
}
