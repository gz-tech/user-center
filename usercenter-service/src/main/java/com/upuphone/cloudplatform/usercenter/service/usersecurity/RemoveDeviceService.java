package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.constants.EmailTemplateEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.remote.email.SendEmailRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.email.model.SendEmailRequest;
import com.upuphone.cloudplatform.usercenter.remote.sms.SendSmsRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsSendRequest;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsType;
import com.upuphone.cloudplatform.usercenter.repo.OauthRefreshTokenRepo;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.RemoveDeviceRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/30
 */
@Service
@Slf4j
public class RemoveDeviceService extends BaseService<RemoveDeviceRequest, Void> {

    @Autowired
    private SendSmsRemoteService sendSmsRetryRemoteService;
    @Autowired
    private SendEmailRemoteService sendEmailRemote;
    @Qualifier("taskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private CommonService commonService;
    @Autowired
    private OauthRefreshTokenRepo refreshTokenRepo;

    @Override
    protected void validate(RemoveDeviceRequest request) {
        // deviceId获取方式变更
        if (Objects.equals(RequestContext.getDeviceId(), request.getDeviceId())) {
            log.error("[RemoveDeviceService]当前使用设备不可移除, req={}",
                    JsonUtility.toJson(request));
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "当前使用设备不可移除");
        }
    }

    @Override
    protected Void processCore(RemoveDeviceRequest soaRequest) {
        Long userId = RequestContext.getUserId();
        UserBaseInfoPo userInfo = commonService.getUserBaseInfoById(userId);
        refreshTokenRepo.removeDevice(soaRequest.getDeviceId(), soaRequest.getId(), userId);
        log.info("[RemoveDeviceService]用户移除授权设备成功, req={}",
                JsonUtility.toJson(soaRequest));
        String removeTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy'年'MM'月'dd'日'HH:mm:ss"));
        String deviceId = soaRequest.getDeviceId();
        String boxingId = userInfo.getBoxingId();
        // send kick message
        if (StringUtils.isNotBlank(userInfo.getSecurityPhoneNumber()) && StringUtils.isNotBlank(userInfo.getSecurityPhoneCode())) {
            sendKickSmsMessage(userInfo.getSecurityPhoneNumber(), userInfo.getSecurityPhoneCode(), boxingId, removeTime, deviceId);
            log.info("[RemoveDeviceService] 发送移除授权设备短信(安全), phoneNumber=[{}], boxingId=[{}], deviceId=[{}]",
                    MaskUtil.maskPhone(userInfo.getSecurityPhoneNumber()), boxingId, deviceId);
            return null;
        }
        if (StringUtils.isNotBlank(userInfo.getPhoneNumber()) && StringUtils.isNotBlank(userInfo.getPhoneCode())) {
            sendKickSmsMessage(userInfo.getPhoneNumber(), userInfo.getPhoneCode(), boxingId, removeTime, deviceId);
            log.info("[RemoveDeviceService] 发送移除授权设备短信(绑定), phoneNumber=[{}], boxingId=[{}], deviceId=[{}]",
                    MaskUtil.maskPhone(userInfo.getPhoneNumber()), boxingId, deviceId);
        }
        if (StringUtils.isNotBlank(userInfo.getEmail())) {
            sendKickEmailMessage(userInfo.getEmail(), boxingId, removeTime, deviceId);
            log.info("[RemoveDeviceService] 发送移除授权设备邮件, email=[{}], boxingId=[{}], deviceId=[{}]",
                    MaskUtil.maskEmail(userInfo.getEmail()), boxingId, deviceId);
        }
        return null;
    }

    private void sendKickSmsMessage(String phoneNumber, String phoneCode, String boxingId, String removeTime, String deviceId) {
        SmsSendRequest smsSendRequest = new SmsSendRequest();
        smsSendRequest.setSmsType(SmsType.AUTH_REMOVED);
        smsSendRequest.setReceiverPhoneNumber(phoneNumber);
        smsSendRequest.setReceiverPhoneCode(phoneCode);
        // 采用异步方式发消息
        smsSendRequest.setIsAsync(true);
        String date = removeTime.substring(0, 11);
        String time = removeTime.substring(11);
        // 帐号名称-移除时间-设备名称
        smsSendRequest.setParams(Arrays.asList(boxingId, date, time, deviceId));
        sendSmsRetryRemoteService.process(smsSendRequest);
    }

    private void sendKickEmailMessage(String email, String boxingId, String removeTime, String deviceId) {
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .subject(EmailTemplateEnum.DEVICE_REMOVED.getSubject())
                .to(email)
                .text(String.format(EmailTemplateEnum.DEVICE_REMOVED.getText(), boxingId, removeTime, deviceId))
                .build();
        CompletableFuture.supplyAsync(() -> sendEmailRemote.process(sendEmailRequest), taskExecutor)
                .whenComplete((smsSendResponse, throwable) -> {
                    if (!smsSendResponse.isSuccess() || null != throwable) {
                        log.error("[RemoveDeviceService] 邮件发送失败", throwable);
                        if (throwable instanceof BusinessException) {
                            throw (BusinessException) throwable;
                        }
                        throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "email send fail");
                    }
                });
    }
}
