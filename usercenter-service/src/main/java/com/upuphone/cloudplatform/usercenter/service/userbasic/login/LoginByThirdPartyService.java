package com.upuphone.cloudplatform.usercenter.service.userbasic.login;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.api.constant.DeviceTypeEnum;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.ThirdPartyAccountEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.model.ThirdPartyAccessTokenBo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.thirdparty.ThirdPartyLoginService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.thirdparty.ThirdPartyStrategyFactory;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountBindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LoginByThirdPartyService extends BaseService<AccountBindingRequest, LoginResponse> {

    @Autowired
    private ThirdPartyStrategyFactory thirdPartyStrategyFactory;

    @Autowired
    private UserThirdPartyAccountMapper userThirdPartyAccountMapper;

    @Autowired
    private LoginCommonService loginCommonService;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Setting setting;

    @Override
    protected void validate(AccountBindingRequest request) {

    }

    @Override
    protected LoginResponse processCore(AccountBindingRequest request) throws Exception {
        ThirdPartyLoginService thirdPartyLoginService =
                thirdPartyStrategyFactory.getThirdPartyLoginServiceByType(ThirdPartyAccountEnum.getByType(request.getBingdingType()).getName());
        ThirdPartyAccessTokenBo thirdPartyAccessTokenBo = thirdPartyLoginService.getThirdPartyAccessTokenByCode(request.getCode());
        UserThirdAccountBaseInfo userInfo = thirdPartyLoginService.getThirdPartyUserInfoByAccessToken(thirdPartyAccessTokenBo);

        UserThirdPartyAccountPo userThirdPartyAccountPo = userThirdPartyAccountMapper.selectOne(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                .eq(UserThirdPartyAccountPo::getUniqueId, userInfo.getUid())
                .eq(UserThirdPartyAccountPo::getType, request.getBingdingType())
                .last("for update"));

        LoginResponse result = new LoginResponse();
        if (null == userThirdPartyAccountPo) {
            //未绑定
            ThirdPartAccountUtil.ThirdPartAuthResult thirdPartAuthResult = new ThirdPartAccountUtil.ThirdPartAuthResult();
            thirdPartAuthResult.setAccountType(ThirdPartyAccountEnum.getByType(request.getBingdingType()));
            thirdPartAuthResult.setDeviceId(RequestContext.getDeviceId());
            thirdPartAuthResult.setDeviceType(DeviceTypeEnum.getByType(Integer.parseInt(RequestContext.getDeviceType())));
            thirdPartAuthResult.setAccountInfo(userInfo);
            String key = userInfo.getUid() + request.getBingdingType() + LocalDateTime.now();
            String redisKey = ThirdPartAccountUtil.getRedisKey(key);
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtility.toJson(thirdPartAuthResult),
                    setting.getThirdPartyBindExpireSeconds(), TimeUnit.SECONDS);
            String ticket = AESUtil.encrypt(key, setting.getThirdLoginTicketAesKey(), setting.getThirdLoginTicketAesIV());
            result.setTicket(ticket);
        } else {
            result = loginCommonService.doLogin(
                    userBaseInfoMapper.selectById(userThirdPartyAccountPo.getUserId()), LoginTypeEnum.THIRD_PART_LOGIN.getType());
        }
        return result;
    }
}
