package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginCommonService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.AccountValidation;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Classname NormalLoginPasswordHandler
 * @Description
 * @Date 2022/4/1 5:52 下午
 * @Created by gz-d
 */
@Component
public class NormalLoginUsePwdHandler implements LoginPwdHandler {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private LoginCommonService loginCommonService;

    @Override
    public UserBaseInfoPo getUser(LoginUsePasswordRequest request) {
        if (InputValidateUtil.isContainChinese(request.getAccount()) ||
                InputValidateUtil.isContainSpace(request.getAccount())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "帐号不能包含空格或中文");
        }
        QueryWrapper<UserBaseInfoPo> query = new QueryWrapper<>();
        if (request.getType() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "请输入登录类型");
        }
        switch (request.getType()) {
            case PHONE_PWD:
                if (AccountValidation.phoneMatches(request.getAccount())) {
                    query.eq("phone_number", request.getAccount());
                    query.eq("phone_code", PhoneUtil.formatPhoneAreaCode(request.getPhoneCode()));
                } else {
                    throw new BusinessException(CommonErrorCode.PARAM_ERROR, "手机号格式错误");
                }
                break;
            case ACCOUNT_EMAIL_PWD:
                if (AccountValidation.emailMatches(request.getAccount())) {
                    query.eq("email", request.getAccount());
                } else if (AccountValidation.boxingIdMatches(request.getAccount())) {
                    query.eq("boxing_id", request.getAccount());
                } else {
                    throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户名格式错误");
                }
                break;
            default:
                throw new BusinessException(UserCenterErrorCode.LOGIN_TYPE_NOT_SUPPORT);
        }
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(query);
        if (userBaseInfoPo != null) {
            return userBaseInfoPo;
        } else {
            throw new BusinessException(UserCenterErrorCode.NOT_REGISTERED_ERROR);
        }
    }

    @Override
    public LoginResponse handleLogin(UserBaseInfoPo user, String secondLoginSecret) {
        return loginCommonService.doLogin(user, LoginTypeEnum.PWD.getType());
    }
}
