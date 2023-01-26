package com.upuphone.cloudplatform.usercenter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserBaseInfoMapper extends BaseMapper<UserBaseInfoPo> {

    UserBaseInfoPo getByUserName(@Param("userName") String userName);

    UserBaseInfoPo getByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    UserBaseInfoPo getByIdForUpdate(@Param("userId") Long userId);

    UserBaseInfoPo getByBoxingIdForUpdate(@Param("boxingId") String boxingId);
}
