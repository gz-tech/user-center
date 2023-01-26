package com.upuphone.cloudplatform.usercenter.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Getter
@Setter
public class Setting {

    @Value("${huawei.sms.url:https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1}")
    private String huaweiSmsUrl;
    @Value("${huawei.sms.appKey:zFVsIb51cVz69peXFF69lZYZG3D6}")
    private String huaweiSmsAppKey; //APP_Key
    @Value("${huawei.sms.appSecret:sDLrrv2yR7YDQGuxlPjK28q0GQ7H}")
    private String huaweiSmsAppSecret; //APP_Secret

    @Value("${user.password.error.maxTimes:10}")
    private Long passwordErrorMaxTimes;


    @Value("${user.validCode.maxTimes:10}")
    private Integer validCodeMaxTimes;

    @Value("${user.password.error.block.hours:4}")
    private Integer passwordErrorBlockHours;

    @Value("${user.validcode.block.hours:4}")
    private Integer validCodeLimitBlockHours;

    @Value("${valid-code.duration:120}")
    private Integer validCodeDuration;

    @Value("${session-token.duration:300}")
    private Integer sessionTokenDuration;

    @Value("${blank-key.duration:60}")
    private Integer blankKeyDuration;

    // 用户登出暂存key失效时间
    @Value("${user.logout.duration:2592000}")
    private Integer userLogoutDuration;

    // 线程池维护线程的最少数量
    @Value("${spring.corePoolSize:32}")
    private Integer corePoolSize;
    // 允许的空闲时间
    @Value("${spring.keepAliveSeconds:60}")
    private Integer keepAliveSeconds;
    // 线程池维护线程的最大数量
    @Value("${spring.maxPoolSize:32}")
    private Integer maxPoolSize;
    // 缓存队列
    @Value("${spring.queueCapacity:160}")
    private Integer queueCapacity;

    @Value("${flash.login.appId:LFR8mvQh}")
    private String appId;

    @Value("${flash.login.appKey:YKI71QIT}")
    private String appKey;

    @Value("${flash.login.url:https://api.253.com/open/flashsdk/mobile-query}")
    private String flashLoginUrl;

    @Value("${refresh.token.sign:bxUsrCntrRfrshTknSign}")
    private String refreshTokenSign;

    @Value("${refresh.token.aes.key:RwcmlVtt}")
    private String refreshTokenAesKey;

    @Value("${refresh.token.aes.iv:4e5Wa71fYoT7ktlg}")
    private String refreshTokenAesIV;

    @Value("${file.upload.token:a2ef406e2c2351e0b9e80029c909242d}")
    private String fileUploadToken;

    @Value("${session.aes.key:RwcmlVtt}")
    private String sessionTokenAesKey;

    @Value("${session.aes.iv:4e5Wa71fYoT7ktlg}")
    private String sessionTokenAesIV;

    @Value("${login.check.valid.duration.seconds:600}")
    private Integer loginCheckExpireSeconds;

    @Value("${login.third.bind.valid.duration.seconds:600}")
    private Integer thirdPartyBindExpireSeconds;

    @Value("${login.third.ticket.aes.key:nLnE4JFFbF6eAzjF}")
    private String thirdLoginTicketAesKey;

    @Value("${login.third.ticket.aes.iv:EJ9iIPhzB4I5UDfv}")
    private String thirdLoginTicketAesIV;
    /**
     * 必须使用oauth2的appid列表
     */
    @Value("${login.oauth.appids}")
    private Set<String> oauthAppIds;

    @Value("${login.share.appid}")
    private Set<String> sharedLoginAppId;

    /**
     * client缓存时间
     */
    @Value("${client.duration:1800}")
    private Integer clientDuration;
    /**
     * 账号app的appid
     */
    @Value("${user.appid:boxing}")
    private String accountAppId;
}
