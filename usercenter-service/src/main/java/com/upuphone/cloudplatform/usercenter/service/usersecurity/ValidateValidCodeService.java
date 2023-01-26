package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.SdkValidateType;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ValidateValidCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidateValidCodeService extends BaseService<ValidateValidCodeRequest, Void> {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Override
    protected void validate(ValidateValidCodeRequest validateValidCodeRequest) {

    }

    @Override
    protected Void processCore(ValidateValidCodeRequest request) throws Exception {
        Long userId = RequestContext.getUserId();
        UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
        if (null == po) {
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        if (request.getType().equals(SdkValidateType.PHONE_VALID_CODE.getType())) {
            String phoneNumberWithAreaCode = commonService.getFormattedPhoneFromUserIdAndType(userId, BoundTypeEnum.BOUND_PHONE.getType());
            validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, phoneNumberWithAreaCode, RequestContext.getDeviceId(),
                    request.getValidCode(), ValidCodeType.SDK_VALIDATE);
        } else if (request.getType().equals(SdkValidateType.EMAIL_VALID_CODE.getType())) {
            validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, commonService.getEmailFromUserId(userId), RequestContext.getDeviceId(),
                    request.getValidCode(), ValidCodeType.SDK_VALIDATE);
        } else {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "校验类型错误");
        }
        return null;
    }
}
