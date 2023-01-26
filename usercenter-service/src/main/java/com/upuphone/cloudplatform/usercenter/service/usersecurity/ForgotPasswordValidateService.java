package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.ForgotPasswordValidateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.VALID_CODE_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/27
 */
@Service
@Slf4j
public class ForgotPasswordValidateService extends BaseService<ForgotPasswordValidateRequest, ForgotPasswordValidateResponse> {

    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Override
    protected void validate(ForgotPasswordValidateRequest request) {
        String validCode = request.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (BoundTypeEnum.isInvalidType(request.getValidType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "发送类型不合法");
        }
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId不能为空");
        }
        if (StringUtils.isBlank(request.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "deviceId不能为空");
        }
    }

    @Override
    protected ForgotPasswordValidateResponse processCore(ForgotPasswordValidateRequest soaRequest) throws Exception {
        // 忘记密码支持安全手机号/手机号/邮箱验证
        return userSecurityUtils.getForgotPasswordValidateResponse(soaRequest, Long.valueOf(soaRequest.getUserId()));
    }
}
