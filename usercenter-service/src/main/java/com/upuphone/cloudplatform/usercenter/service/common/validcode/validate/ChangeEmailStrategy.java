package com.upuphone.cloudplatform.usercenter.service.common.validcode.validate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/26
 */
@Component
@Slf4j
public class ChangeEmailStrategy implements ValidCodeValidateStrategy<EmailSendValidCodeRequest> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public void validate(EmailSendValidCodeRequest request) {
        Long count = userBaseInfoMapper.selectCount(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getEmail, request.getEmailAddress()));
        if (count >= 1) {
            log.error("邮箱重复，request={}", JsonUtility.toJson(request));
            throw new BusinessException(UserCenterErrorCode.BIND_EMAIL_DUPLICATED);
        }
    }
}
