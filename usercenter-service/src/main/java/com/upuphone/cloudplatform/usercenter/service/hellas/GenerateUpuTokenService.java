package com.upuphone.cloudplatform.usercenter.service.hellas;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.api.constant.RemoteSourceEnum;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.HellasErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.hellas.util.CheckTokenUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginCommonService;
import com.upuphone.cloudplatform.usercenter.service.util.AccessTokenUtil;
import com.upuphone.cloudplatform.usercenter.service.util.RefreshTokenUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.GenerateUpuTokenRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.GenerateUpuTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/12
 */
@Service
@Slf4j
public class GenerateUpuTokenService extends BaseService<GenerateUpuTokenRequest, GenerateUpuTokenResponse> {

    @Autowired
    private AccessTokenUtil accessTokenUtil;
    @Autowired
    private RefreshTokenUtil refreshTokenUtil;
    @Autowired
    private Setting setting;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private LoginCommonService loginCommonService;
    @Autowired
    private CheckTokenUtil checkTokenUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    protected void validate(GenerateUpuTokenRequest request) {

    }

    @Override
    protected GenerateUpuTokenResponse processCore(GenerateUpuTokenRequest request) throws Exception {
        String lockKey = RedisKeys.accessTokenLockKey(request.getUserId(), RequestContext.getAppId(), RequestContext.getDeviceId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 20, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            checkTokenUtil.checkToken(request.getLotusAccessToken(), request.getLotusId(), request.getMobile(), RemoteSourceEnum.LOTUS);
            UserBaseInfoPo existOne = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                    .eq(UserBaseInfoPo::getId, request.getUserId()));
            if (existOne == null) {
                throw new BusinessException(HellasErrorCode.UPU_USER_NOT_EXIST);
            }
            OauthRefreshTokenPo existRefreshTokenPo = loginCommonService.hasLoginCheck(Long.parseLong(request.getUserId()));
            String accessToken, refreshToken;
            if (existRefreshTokenPo != null) {
                accessToken = tokenService.generateAccessToken(Long.parseLong(request.getUserId()), existRefreshTokenPo.getId());
                refreshToken = existRefreshTokenPo.getRefreshToken();
            } else {
                List<String> tokens = tokenService.tokenPersistence(Long.parseLong(request.getUserId()));
                accessToken = tokens.get(0);
                refreshToken = tokens.get(1);
            }
            GenerateUpuTokenResponse response = new GenerateUpuTokenResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setUserId(request.getUserId());
            response.setAccessTokenExpiresAt(DateUtil.asDate(
                    accessTokenUtil.parseToken(response.getAccessToken()).getExpireTime()).getTime());
            response.setRefreshTokenExpiresAt(DateUtil.asDate(
                    refreshTokenUtil.parseToken(AESUtil.decrypt(response.getRefreshToken(),
                            setting.getRefreshTokenAesKey(),
                            setting.getRefreshTokenAesIV()),
                            setting.getRefreshTokenSign()).getExpireTime()).getTime());
            return response;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
