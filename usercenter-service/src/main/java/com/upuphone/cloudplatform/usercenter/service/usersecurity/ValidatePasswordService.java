package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.util.PasswordUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ValidatePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidatePasswordService extends BaseService<ValidatePasswordRequest, Void> {

    @Autowired
    private CommonService commonService;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    protected void validate(ValidatePasswordRequest validatePasswordRequest) {

    }

    @Override
    protected Void processCore(ValidatePasswordRequest validatePasswordRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
        if (null == po) {
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        passwordUtil.checkPassword(userId, validatePasswordRequest.getPassword(), po.getPassword());
        return null;
    }
}
