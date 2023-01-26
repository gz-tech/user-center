package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP_MSG;

@Service
@Slf4j
public class ForgotPasswordLoggedInService extends BaseService<ForgotPasswordRequest, Void> {

    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Override
    protected void validate(ForgotPasswordRequest request) {
        if (InputValidateUtil.isContainSpace(request.getNewPwd()) || InputValidateUtil.isContainChinese(request.getNewPwd())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, PWD_EXP_MSG);
        }
    }

    @Override
    protected Void processCore(ForgotPasswordRequest soaRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        userSecurityUtils.processForgotPassword(soaRequest, userId);
        return null;
    }
}
