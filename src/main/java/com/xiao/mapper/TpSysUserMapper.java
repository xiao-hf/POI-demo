package com.xiao.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.xiao.domain.TpSysUser;

public interface TpSysUserMapper {
    List<TpSysUser> selectAll();


    int deleteByPrimaryKey(Long id);

    int insert(TpSysUser record);

    int insertSelective(TpSysUser record);

    TpSysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TpSysUser record);

    int updateByPrimaryKey(TpSysUser record);
}