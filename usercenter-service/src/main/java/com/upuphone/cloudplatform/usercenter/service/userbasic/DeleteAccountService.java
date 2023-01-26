package com.upuphone.cloudplatform.usercenter.service.userbasic;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.repo.UserInfoRepo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.util.PasswordUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.DeleteAccountRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author guangzheng.ding
 * @date 2021/12/23 9:51
 */
@Service
public class DeleteAccountService extends BaseService<DeleteAccountRequest, Void> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private UserThirdPartyAccountMapper userThirdPartyAccountMapper;

    @Override
    protected void validate(DeleteAccountRequest request) {
        if (StringUtils.isBlank(RequestContext.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id can not be null");
        }
        if (null == RequestContext.getUserId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user id不能为空");
        }
    }

    @SneakyThrows
    @Override
    protected Void processCore(DeleteAccountRequest request) {
        String deviceId = RequestContext.getDeviceId();
        Long userId = RequestContext.getUserId();
        String lockKey = RedisKeyUtils.getIdempotentCheckKey(LoginTypeEnum.DELETE_ACCOUNT, userId, deviceId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
            if (po == null) {
                throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
            }
            passwordUtil.checkPassword(userId, request.getPassword(), po.getPassword());
            userInfoRepo.deleteAccount(po);
            userThirdPartyAccountMapper.delete(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                    .eq(UserThirdPartyAccountPo::getUserId, userId));
            return null;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
