package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.usercenter.auth.vo.checkapprovestate.CheckApproveStateResponse;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthApprovalPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthApprovalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/27
 */
@Service
@Slf4j
public class CheckApproveStateService extends BaseService<Void, CheckApproveStateResponse> {

    @Autowired
    private OauthApprovalMapper approvalMapper;

    @Override
    protected void validate(Void checkApproveStateRequest) {

    }

    @Override
    protected CheckApproveStateResponse processCore(Void checkApproveStateRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        String appId = RequestContext.getAppId();
        // 暂时不用 deviceId+deviceType
        String deviceId = RequestContext.getDeviceId();
        String deviceType = RequestContext.getDeviceType();
        LocalDateTime now = LocalDateTime.now();
        Long count = approvalMapper.selectCount(Wrappers.<OauthApprovalPo>lambdaQuery()
                .eq(OauthApprovalPo::getUserId, userId)
                .eq(OauthApprovalPo::getAppId, appId)
                .ge(OauthApprovalPo::getExpiresAt, now));
        return new CheckApproveStateResponse().setState(count > 0);
    }
}
