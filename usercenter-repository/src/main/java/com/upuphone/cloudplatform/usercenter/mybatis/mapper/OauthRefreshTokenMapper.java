package com.upuphone.cloudplatform.usercenter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * refresh_token Mapper 接口
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Mapper
public interface OauthRefreshTokenMapper extends BaseMapper<OauthRefreshTokenPo> {
    List<OauthRefreshTokenPo> getByUserIdAndDevice(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    OauthRefreshTokenPo getByAccessToken(@Param("accessToken") String accessToken);

    List<OauthRefreshTokenPo> getValidTokens(@Param("userId") Long userId);

    List<OauthRefreshTokenPo> getAllDevicesByUserId(@Param("userId") Long userId);

    List<OauthRefreshTokenPo> getByRefreshIdsIncludeDeleted(@Param("ids") Collection<Long> refreshTokenIds);
}
