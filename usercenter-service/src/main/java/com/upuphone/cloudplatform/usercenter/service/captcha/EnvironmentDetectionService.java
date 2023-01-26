package com.upuphone.cloudplatform.usercenter.service.captcha;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.EnvironmentDetectionRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.EnvironmentDetectionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @Classname EnvironmentDetectionService
 * @Description 判断是否需要环境检测
 * @Date 2022/6/1 10:40 上午
 * @Created by gz-d
 */
@Service
@Slf4j
public class EnvironmentDetectionService extends BaseService<EnvironmentDetectionRequest, EnvironmentDetectionResponse> {
    @Autowired
    private CaptchaUtil captchaUtil;

    @Override
    protected void validate(EnvironmentDetectionRequest request) {

    }

    @Override
    protected EnvironmentDetectionResponse processCore(EnvironmentDetectionRequest request) throws Exception {
        String uniqId = null;
        if (Objects.equals(request.getCaptchaBusinessType(), CaptchaBusinessEnum.SECURITY_MODULE)) {
            //security类型有登录态和非登录态，登录态用token解析userId，非登录态用传惨userI。
            if (null == RequestContext.getUserId() && StringUtils.isEmpty(request.getUserId())) {
                throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId 不为空");
            }
            uniqId = !StringUtils.isEmpty(request.getUserId()) ? request.getUserId() : RequestContext.getUserId().toString();
        } else if (Objects.equals(request.getCaptchaBusinessType(), CaptchaBusinessEnum.REGIESTER_MODULE.getType())) {
            if (!StringUtils.isEmpty(request.getPhoneNumber())) {
                uniqId = PhoneUtil.formatPhoneNumber(request.getPhoneCode(), request.getPhoneNumber());
            } else if (!StringUtils.isEmpty(request.getEmail())) {
                uniqId = request.getEmail();
            } else {
                log.error("注册验证手机号/邮箱不为空");
                throw new BusinessException(CommonErrorCode.PARAM_ERROR);
            }
        }
        CaptchaValidateBo bo = new CaptchaValidateBo();
        bo.setCaptchaBusinessType(request.getCaptchaBusinessType());
        bo.setUniqueId(uniqId);
        boolean needDetection = captchaUtil.ifNeedCaptcha(bo);
        EnvironmentDetectionResponse response = new EnvironmentDetectionResponse();
        response.setNeedDetection(needDetection);
        return response;
    }
}
