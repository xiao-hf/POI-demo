package com.xiao.domain;

import java.util.Date;
import lombok.Data;

@Data
public class TpSysUser {
    private Long id;

    /**
    * 账号
    */
    private String account;

    /**
    * 名称
    */
    private String name;

    private String phone;

    /**
    * api推送url
    */
    private String alarmApiUrl;

    /**
    * kafka推送地址
    */
    private String alarmKafkaServer;

    /**
    * kafka推送topic
    */
    private String alarmKafkaTopic;

    /**
    * 推送方式 (API/KAFKA)
    */
    private String alarmPutType;

    /**
    * 备注 布控原因
    */
    private String remark;

    private Integer limit;

    /**
    * 推送开关
    */
    private Boolean alarmStatus;

    /**
    * 账号启用开关
    */
    private Boolean enable;

    private String token;

    private Date expireTime;

    /**
    * 密码
    */
    private String passWord;

    /**
    * 系统IP地址
    */
    private String sysIp;

    /**
    * 单位编号
    */
    private String depNo;

    /**
    * 单位名称
    */
    private String depName;
}