package com.upuphone.cloudplatform.usercenter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 第三方帐户表 Mapper 接口
 * </p>
 *
 * @author zhumeng.han
 * @since 2022-04-13
 */
@Mapper
public interface UserThirdPartyAccountMapper extends BaseMapper<UserThirdPartyAccountPo> {
    /**
     *
     * @param phoneNumber
     * @param type
     * @return UserThirdPartyAccountPo
     */
    UserThirdPartyAccountPo selectByPhoneNumberAndType(@Param("phoneNumber")String phoneNumber, @Param("type")int type);

}
