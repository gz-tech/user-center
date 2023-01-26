package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.ApiConstants;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.MaskTypeEnum;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.MobileAndEmailRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.MobileAndEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Service
@Slf4j
public class GetMobileAndEmailService extends BaseService<MobileAndEmailRequest, MobileAndEmailResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Override
    protected void validate(MobileAndEmailRequest soaRequest) {
        if (null != RequestContext.getUserId()) {
            throw new BusinessException(UserCenterErrorCode.ALREADY_LOGIN);
        }
        if (StringUtils.isBlank(soaRequest.getBoxingId())
                && StringUtils.isBlank(soaRequest.getMobile())
                && StringUtils.isBlank(soaRequest.getEmail())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "boxingId、mobile与email不能同时为空");
        }
    }

    @Override
    protected MobileAndEmailResponse processCore(MobileAndEmailRequest soaRequest) throws Exception {
        UserBaseInfoPo po;
        String maskMobileEmailUserKey;
        if (StringUtils.isNotBlank(soaRequest.getBoxingId())) {
            maskMobileEmailUserKey = RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.BOXING_ID, soaRequest.getBoxingId());
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(maskMobileEmailUserKey))) {
                return userSecurityUtils.getMobileAndEmailFromRedis(soaRequest, maskMobileEmailUserKey);
            }
            po = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                    .eq(UserBaseInfoPo::getBoxingId, soaRequest.getBoxingId()));
        } else if (StringUtils.isNotBlank(soaRequest.getMobile())) {
            String areaCode = soaRequest.getAreaCode();
            if (StringUtils.isBlank(areaCode)) {
                areaCode = ApiConstants.AREA_CODE_CHINA;
            }
            String formatted = PhoneUtil.formatPhoneNumber(areaCode, soaRequest.getMobile());
            maskMobileEmailUserKey = RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.MOBILE, formatted);
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(maskMobileEmailUserKey))) {
                return userSecurityUtils.getMobileAndEmailFromRedis(soaRequest, maskMobileEmailUserKey);
            }
            po = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                    .eq(UserBaseInfoPo::getPhoneCode, PhoneUtil.formatPhoneAreaCode(soaRequest.getAreaCode()))
                    .eq(UserBaseInfoPo::getPhoneNumber, soaRequest.getMobile()));
        } else {
            maskMobileEmailUserKey = RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.EMAIL, soaRequest.getEmail());
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(maskMobileEmailUserKey))) {
                return userSecurityUtils.getMobileAndEmailFromRedis(soaRequest, maskMobileEmailUserKey);
            }
            po = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                    .eq(UserBaseInfoPo::getEmail, soaRequest.getEmail()));
        }
        if (null == po) {
            log.error("[GetBindOrSafeMobileService]用户不存在，req={}", JsonUtility.toJson(soaRequest));
            stringRedisTemplate.opsForValue().set(maskMobileEmailUserKey, StringUtils.EMPTY, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        return userSecurityUtils.getMobileAndEmailResponseFromUserPo(po, maskMobileEmailUserKey);
    }
}
