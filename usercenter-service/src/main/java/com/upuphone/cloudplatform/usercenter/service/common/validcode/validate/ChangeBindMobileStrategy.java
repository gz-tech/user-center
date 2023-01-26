package com.upuphone.cloudplatform.usercenter.service.common.validcode.validate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/25
 */
@Component
@Slf4j
public class ChangeBindMobileStrategy implements ValidCodeValidateStrategy<SendValidCodeRequest> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public void validate(SendValidCodeRequest request) {
        String phoneCode = PhoneUtil.formatPhoneAreaCode(request.getPhoneCode());
        String phoneNumber = request.getPhoneNumber();
        Long count = userBaseInfoMapper.selectCount(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getPhoneCode, phoneCode)
                .eq(UserBaseInfoPo::getPhoneNumber, phoneNumber));
        if (count >= 1) {
            log.error("绑定手机号重复, request={}", JsonUtility.toJson(request));
            throw new BusinessException(UserCenterErrorCode.BIND_MOBILE_DUPLICATED);
        }
    }
}
