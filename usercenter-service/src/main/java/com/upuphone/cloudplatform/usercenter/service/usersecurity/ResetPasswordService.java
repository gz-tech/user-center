package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.SessionUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ResetPasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Service
@Slf4j
public class ResetPasswordService extends BaseService<ResetPasswordRequest, Boolean> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(ResetPasswordRequest resetPasswordRequest) {

    }

    @Override
    protected Boolean processCore(ResetPasswordRequest resetPasswordRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_PASSWORD_SESSION, String.valueOf(userId));
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[ResetPasswordService]修改密码时session-token已过期或不符, req={}, userId=[{}], key=[{}], token=[{}]",
                    JsonUtility.toJson(resetPasswordRequest), userId, sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
        po.setPassword(BCrypt.hashpw(resetPasswordRequest.getNewPwd(), BCrypt.gensalt()));
        if (0 == userBaseInfoMapper.updateById(po)) {
            log.error("[ResetPasswordService]修改密码失败, userId=[{}] request={}",
                    userId, JsonUtility.toJson(resetPasswordRequest));
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "修改密码失败");
        }
        log.info("[ResetPasswordService]用户修改密码成功, userId=[{}]", userId);
        stringRedisTemplate.delete(sessionKey);
        return true;
    }
}
