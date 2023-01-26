package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterModeService extends BaseService<Void, String> {
    @Autowired
    private RegisterSetting registerSetting;

    @Override
    protected void validate(Void unused) {

    }

    @Override
    protected String processCore(Void unused) throws Exception {
        return registerSetting.getMode();
    }
}
