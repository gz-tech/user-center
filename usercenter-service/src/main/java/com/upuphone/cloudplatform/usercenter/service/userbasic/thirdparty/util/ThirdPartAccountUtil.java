package com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util;

import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.api.constant.DeviceTypeEnum;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.ThirdPartyAccountEnum;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo.AccountBindingRepo;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartAccountUtil {

    private static final String REDIS_BUSINESS = "THIRD_PART";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private AccountBindingRepo accountBindingRepo;

    public static String getRedisKey(String ticket) {
        return RedisKeyUtils.getRedisKey(REDIS_BUSINESS, ticket);
    }

    @SneakyThrows
    public ThirdPartAuthResult getTPAccountFromRedis(String redisKey) {

        String accountStr = stringRedisTemplate.opsForValue().get(redisKey);

        ThirdPartAuthResult result = JsonUtility.toObject(accountStr, ThirdPartAuthResult.class);

        return result;
    }
    public ThirdPartAccountUtil.ThirdPartAuthResult getThirdPartAuthResultFromTicket(String ticket) {
        String thirdBindRedisKey = ThirdPartAccountUtil.getRedisKey(
                AESUtil.decrypt(ticket, setting.getThirdLoginTicketAesKey(), setting.getThirdLoginTicketAesIV()));
        ThirdPartAccountUtil.ThirdPartAuthResult thirdPartAuthResult = this.getTPAccountFromRedis(thirdBindRedisKey);
        return  thirdPartAuthResult;
    }

    public void loginCheckThirdPartBinded(String thirdPartBindTicket, Long userId) {
        if (Strings.isNotBlank(thirdPartBindTicket)) {
            ThirdPartAccountUtil.ThirdPartAuthResult thirdPartAuthResult =
                    this.getThirdPartAuthResultFromTicket(thirdPartBindTicket);
            accountBindingRepo.bindedCheck(userId, thirdPartAuthResult.getAccountInfo().getUid(), thirdPartAuthResult.getAccountType());
        }
    }

    @Getter
    @Setter
    public static class ThirdPartAuthResult {
        private UserThirdAccountBaseInfo accountInfo;
        private String deviceId;
        private DeviceTypeEnum deviceType;
        private ThirdPartyAccountEnum accountType;
    }
}
