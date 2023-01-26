package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.auth.vo.AuthErrorCode;
import com.upuphone.cloudplatform.usercenter.auth.vo.authcode.GenerateAuthCodeRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.authcode.GenerateAuthCodeResponse;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthApprovalPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthApprovalMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.service.authentication.util.AuthCodeUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/28
 */
@Service
@Slf4j
public class GenerateAuthCodeService extends BaseService<GenerateAuthCodeRequest, GenerateAuthCodeResponse> {

    @Autowired
    private OauthClientDetailMapper clientDetailMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private Setting setting;
    @Autowired
    private AuthCodeUtil authCodeUtil;
    @Autowired
    private OauthApprovalMapper approvalMapper;

    @Override
    protected void validate(GenerateAuthCodeRequest request) {
        if (!setting.getOauthAppIds().contains(request.getAppId())) {
            throw new BusinessException(AuthErrorCode.APP_OAUTH_FORBIDDEN);
        }
        if (Strings.isBlank(request.getRedirectUri())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "重定向uri不为空");
        }
    }

    @Override
    protected GenerateAuthCodeResponse processCore(GenerateAuthCodeRequest request) throws Exception {
        Long userId = RequestContext.getUserId();
        // Long userId = 1491349126433558529L;
        String appId = request.getAppId();
        String redirectUri = request.getRedirectUri();
        String lockKey = RedisKeys.authCodeLockKey(userId.toString(), appId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            OauthClientDetailPo clientPo = clientDetailMapper.getByClientId(appId);
            if (null == clientPo) {
                throw new BusinessException(AuthErrorCode.APP_NOT_REGISTERED);
            }
            //TODO
            /*if (!redirectUri.toLowerCase().startsWith(clientPo.getWebServerRedirectUri().toLowerCase())) {
                throw new BusinessException(AuthErrorCode.INVALID_REDIRECT_URI);
            }*/
            // 是否属于授权态
            Long count = approvalMapper.selectCount(Wrappers.<OauthApprovalPo>lambdaQuery()
                    .eq(OauthApprovalPo::getUserId, userId)
                    .eq(OauthApprovalPo::getAppId, appId)
                    .ge(OauthApprovalPo::getExpiresAt, LocalDateTime.now()));
            if (count <= 0) {
                if (!clientPo.getAutoApprove()) {
                    // todo 这里应该重定向到授权页面 但是目前没有开发计划
                    throw new BusinessException(AuthErrorCode.APPROVE_STATE_NOT_FOUND);
                }
                // 自动授权 授权有效期取refreshToken有效期
                if (0 == autoApprove(userId, appId, clientPo.getRefreshTokenValidity())) {
                    log.error("自动授权失败, userId={}, appId={}, clientPo={}", userId, appId, JsonUtility.toJson(clientPo));
                    throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "自动授权失败");
                }
            }
            String code = authCodeUtil.generateIfAbsent(request.getScope(), clientPo.getCodeValidity());
            String state = request.getState();
            return new GenerateAuthCodeResponse().setCode(code).setRedirectUri(redirectUri).setState(state);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private int autoApprove(Long userId, String appId, long approveValidity) {
        OauthApprovalPo approvalPo = new OauthApprovalPo();
        approvalPo.setUserId(userId);
        approvalPo.setAppId(appId);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(approveValidity);
        approvalPo.setExpiresAt(expiresAt);
        return approvalMapper.insert(approvalPo);
    }
}
