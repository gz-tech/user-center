package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.utils.ListUtil;
import com.upuphone.cloudplatform.usercenter.setting.PhoneAreaCodesSetting;
import com.upuphone.cloudplatform.usercenter.vo.PhoneAreaCode;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.PhoneAreaCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class GetPhoneAreaCodeService extends BaseService<Void, PhoneAreaCodeResponse> {

    @Autowired
    private PhoneAreaCodesSetting phoneAreaCodesSetting;


    @Override
    protected void validate(Void getUserDetailRequest) {

    }

    @Override
    protected PhoneAreaCodeResponse processCore(Void getUserDetailRequest) throws Exception {

        List<PhoneAreaCode> phoneAreaCodes = phoneAreaCodesSetting.getPhoneAreaCodes();

        PhoneAreaCodeResponse result = new PhoneAreaCodeResponse();

        if (ListUtil.any(phoneAreaCodes)) {
            phoneAreaCodes.sort(Comparator.comparingInt(v -> v.getInitial().charAt(0)));
        }

        result.setPhoneAreaCodes(phoneAreaCodes);

        return result;
    }
}
