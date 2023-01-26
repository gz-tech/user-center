package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Service
@Slf4j
public class ForgotPasswordService extends BaseService<ForgotPasswordRequest, Void> {

    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Override
    protected void validate(ForgotPasswordRequest request) {
        if (InputValidateUtil.isContainChinese(request.getNewPwd()) || InputValidateUtil.isContainSpace(request.getNewPwd())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, PWD_EXP_MSG);
        }
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId不能为空");
        }
    }

    @Override
    protected Void processCore(ForgotPasswordRequest soaRequest) throws Exception {
        userSecurityUtils.processForgotPassword(soaRequest, Long.valueOf(soaRequest.getUserId()));
        return null;
    }
}
