package com.upuphone.cloudplatform.usercenter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Oath client detail Mapper 接口
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Mapper
public interface OauthClientDetailMapper extends BaseMapper<OauthClientDetailPo> {
    OauthClientDetailPo getByClientId(@Param("appId") String clientId);
}
