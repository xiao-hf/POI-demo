package com.xiao.domain;

import java.util.Date;
import lombok.Data;

@Data
public class AlarmInfoOldOne {
    private String id;

    /**
    * 告警id
    */
    private String alarmId;

    /**
    * 来源（商汤|旷视）
    */
    private String alarmSource;

    /**
    * 相机id
    */
    private String alarmDeviceId;

    /**
    * 相机国标id
    */
    private String alarmDeviceCode;

    /**
    * 相机名称
    */
    private String alarmDeviceName;

    /**
    * 人员id
    */
    private String alarmPersonId;

    /**
    * 人员身份证
    */
    private String alarmPersonCode;

    /**
    * 人员编号
    */
    private String alarmPersonNo;

    /**
    * 人员姓名
    */
    private String alarmPersonName;

    /**
    * 布控图
    */
    private String alarmPersonUrl;

    /**
    * 小图
    */
    private String alarmSmallImg;

    /**
    * 大图
    */
    private String alarmBigImg;

    /**
    * 相似度
    */
    private Double similarityDegree;

    /**
    * 源url
    */
    private String originalUrl;

    /**
    * 抓拍时间
    */
    private Date alarmTime;

    /**
    * 插入时间
    */
    private Date createDate;

    /**
    * 创建人
    */
    private Long createUserId;
}