package com.upuphone.cloudplatform.usercenter.setting;

import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.vo.PhoneAreaCode;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
public class PhoneAreaCodesSetting {


    @SneakyThrows
    public List<PhoneAreaCode> getPhoneAreaCodes() {

        ConfigFile configFile = ConfigService.getConfigFile("phoneCode", ConfigFileFormat.JSON);

        String content = configFile.getContent();
        List<PhoneAreaCode> phoneAreaCodes = JsonUtility.toList(content, PhoneAreaCode.class);

        return phoneAreaCodes;
    }
}
