package com.upuphone.cloudplatform.usercenter.repo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserInfoRepo {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;
    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Transactional(timeout = 10)
    public boolean setUserBoxingId(Long userId, String boxingId) {

        UserBaseInfoPo userBaseInfoPoOfBoxingId = userBaseInfoMapper.getByBoxingIdForUpdate(boxingId);
        if (userBaseInfoPoOfBoxingId != null && !userBaseInfoPoOfBoxingId.getId().equals(userId)) {
            throw new BusinessException(UserCenterErrorCode.DUPLICATE_BOXING_ID);
        }
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.getByIdForUpdate(userId);
        if (!Strings.isNullOrEmpty(userBaseInfoPo.getBoxingId())) {
            if (userBaseInfoPo.getBoxingId().equals(boxingId)) {
                return true;
            }
            throw new BusinessException(UserCenterErrorCode.DUPLICATE_BOXING_ID);
        }
        UserBaseInfoPo userBaseInfoPoUpdater = new UserBaseInfoPo();
        userBaseInfoPoUpdater.setId(userId);
        userBaseInfoPoUpdater.setBoxingId(boxingId);
        userBaseInfoMapper.updateById(userBaseInfoPoUpdater);
        return true;
    }

    @Transactional(rollbackFor = Exception.class, timeout = 15)
    public void deleteAccount(UserBaseInfoPo po) {
        Long userId = po.getId();
        if (0 == userBaseInfoMapper.deleteById(po)) {
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "注销失败");
        }
        // 删除所有该userId的refreshToken
        Set<Long> refreshTokenIds = refreshTokenMapper
                .selectObjs(Wrappers.<OauthRefreshTokenPo>lambdaQuery()
                        .select(OauthRefreshTokenPo::getId)
                        .eq(OauthRefreshTokenPo::getUserId, userId)
                        .last("for update"))
                .stream()
                .map(o -> ((BigInteger) o).longValue())
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(refreshTokenIds)) {
            refreshTokenMapper.deleteBatchIds(refreshTokenIds);
            userSecurityUtils.batchAddAccountDeleteKey(refreshTokenIds);
        }
    }
}
