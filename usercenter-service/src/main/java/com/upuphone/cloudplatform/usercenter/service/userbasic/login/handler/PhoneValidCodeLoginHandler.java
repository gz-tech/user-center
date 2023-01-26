package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginCommonService;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Classname PhoneValidCodeLoginHandler
 * @Description
 * @Date 2022/4/1 7:40 下午
 * @Created by gz-d
 */
@Component
public class PhoneValidCodeLoginHandler implements LoginValidCodeHandler {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private LoginCommonService loginCommonService;

    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Override
    public UserBaseInfoPo getUser(LoginUseValidCodeRequest request) {
        QueryWrapper<UserBaseInfoPo> userQueryMapper = new QueryWrapper<>();
        userQueryMapper.eq("phone_number", request.getPhoneNumber());
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(userQueryMapper);
        if (userBaseInfoPo != null) {
            return userBaseInfoPo;
        } else {
            throw new BusinessException(UserCenterErrorCode.NOT_REGISTERED_ERROR);
        }
    }

    @Override
    public LoginResponse handleLogin(UserBaseInfoPo user, LoginUseValidCodeRequest request) {
        String phoneNumberWithAreaCode = PhoneUtil.formatPhoneNumber(request.getPhoneCode(),
                request.getPhoneNumber());
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, phoneNumberWithAreaCode, RequestContext.getDeviceId(),
                request.getValidCode(), LoginTypeEnum.PHONE_VALIDCODE.getValidCodeType());
        return loginCommonService.doLogin(user, LoginTypeEnum.PHONE_VALIDCODE.getType());
    }
}
