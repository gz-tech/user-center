package com.upuphone.cloudplatform.usercenter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthAccessTokenPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * access_token Mapper 接口
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Mapper
public interface OauthAccessTokenMapper extends BaseMapper<OauthAccessTokenPo> {
    List<OauthAccessTokenPo> getByUserId(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    OauthAccessTokenPo getByToken(@Param("accessToken") String accessToken);
}
