package com.upuphone.cloudplatform.usercenter.service.hellas;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.auth.vo.AuthErrorCode;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.util.AccessTokenUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.TokenValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.TokenValidateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * lotus校验tokenService
 */
@Component
@Slf4j
public class LotusAccessTokenService extends BaseService<TokenValidateRequest, TokenValidateResponse> {

    @Autowired
    private AccessTokenUtil accessTokenUtil;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    protected void validate(TokenValidateRequest request) {
        if (Strings.isNullOrEmpty(request.getUpuAccessToken())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "access token can not be null");
        }
    }

    @Override
    protected TokenValidateResponse processCore(TokenValidateRequest request) throws Exception {

        TokenBo tokenBO = accessTokenUtil.parseToken(request.getUpuAccessToken());

        if (tokenBO.getExpired()) {
            throw new BusinessException(AuthErrorCode.TOKEN_INVALID_EXPIRATION);
        }

        //校验手机号合法性
        checkMobile(request.getMobile(), tokenBO.getUserId());

        return new TokenValidateResponse(tokenBO.getUserId());
    }

    private void checkMobile(String mobile, String userId) {

        if (StringUtils.isEmpty(mobile)) {
            return;
        }

        UserBaseInfoPo user = userBaseInfoMapper.selectById(Long.valueOf(userId));

        if (user == null) {
            log.info("LotusAccessTokenService.checkMobile,user is null,userId:{}", userId);
            throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
        }

        if (!StringUtils.equals(user.getPhoneNumber(), mobile)) {
            log.info("LotusAccessTokenService.checkMobile,userId:{},input mobile:{}", userId, mobile);
            throw new BusinessException(AuthErrorCode.USER_MOBILE_NOT_MATCH);
        }
    }
}
