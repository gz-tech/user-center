package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/28
 */
@Service
@Slf4j
public class JudgeOauthAppService extends BaseService<Void, Boolean> {

    @Autowired
    private Setting setting;

    @Override
    protected void validate(Void request) {

    }

    @Override
    protected Boolean processCore(Void request) throws Exception {
        String appId = RequestContext.getAppId();
        Set<String> oauthAppIds = setting.getOauthAppIds();
        return oauthAppIds.contains(appId);
    }
}
