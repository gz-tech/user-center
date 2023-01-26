package com.upuphone.cloudplatform.usercenter.service.oauth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthCodePo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthCodeMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.oauth.util.RandomValueStringGenerator;
import com.upuphone.cloudplatform.usercenter.vo.request.oauth.AuthorizationCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.oauth.AuthorizationCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAuthoricationCodeService extends BaseService<AuthorizationCodeRequest, AuthorizationCodeResponse> {

    @Autowired
    private OauthClientDetailMapper oauthClientDetailMapper;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private OauthCodeMapper oauthCodeMapper;

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    @Override
    protected void validate(AuthorizationCodeRequest request) {
        if (Strings.isNullOrEmpty(request.getUserId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user id can not be null");
        }
        if (Strings.isNullOrEmpty(request.getAppId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "app id can not be null");
        }
        if (Strings.isNullOrEmpty(request.getAccessToken())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "accessToken can not be null");
        }
    }


    @Override
    protected AuthorizationCodeResponse processCore(AuthorizationCodeRequest soaRequest) throws Exception {

        QueryWrapper<OauthClientDetailPo> clientDetailPoQueryWrapper = new QueryWrapper<>();
        clientDetailPoQueryWrapper.eq("app_id", soaRequest.getAppId());

        OauthClientDetailPo oauthClientDetailPo = oauthClientDetailMapper.selectOne(clientDetailPoQueryWrapper);

        if (oauthClientDetailPo == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "appId not exist");
        }

        QueryWrapper<UserBaseInfoPo> userBaseQueryWrapper = new QueryWrapper<>();
        userBaseQueryWrapper.eq("id", soaRequest.getUserId());
        //        userBaseQueryWrapper.eq("del_flag", DeletedFlag.IN_USE);
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(userBaseQueryWrapper);
        if (userBaseInfoPo == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user not exist");
        }

        OauthCodePo oauthCodePo = new OauthCodePo();
        oauthCodePo.setAppId(soaRequest.getAppId());
        oauthCodePo.setUserId(soaRequest.getUserId());
        oauthCodePo.setAuthentication(null);
        String code = generator.generate();
        oauthCodePo.setCode(code);
        oauthCodeMapper.insert(oauthCodePo);

        AuthorizationCodeResponse result = new AuthorizationCodeResponse();
        result.setCode(code);

        return result;
    }
}
