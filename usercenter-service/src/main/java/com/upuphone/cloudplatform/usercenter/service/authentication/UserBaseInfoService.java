package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.auth.vo.getuserbaseinfo.GetUserBaseInfoResponse;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.userbasic.converter.UserInfoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */
@Service
@Slf4j
public class UserBaseInfoService extends BaseService<Void, GetUserBaseInfoResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    protected void validate(Void req) {
    }

    @Override
    protected GetUserBaseInfoResponse processCore(Void request) throws Exception {
        UserBaseInfoPo po = userBaseInfoMapper.selectById(RequestContext.getUserId());
        if (null == po) {
            log.error("[UserBaseInfoService] 用户不存在, userId=[{}]", RequestContext.getUserId());
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        GetUserBaseInfoResponse response = new GetUserBaseInfoResponse();
        response.setUserInfo(UserInfoConverter.userBaseInfoPo2UserInfo(po));
        return response;
    }
}
