package com.xiao.mapper;

import com.xiao.domain.AlarmInfoOldOne;

public interface AlarmInfoOldOneMapper {
    int deleteByPrimaryKey(String id);

    int insert(AlarmInfoOldOne record);

    int insertSelective(AlarmInfoOldOne record);

    AlarmInfoOldOne selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AlarmInfoOldOne record);

    int updateByPrimaryKey(AlarmInfoOldOne record);
}