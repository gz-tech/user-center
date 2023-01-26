package com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;

import java.io.IOException;

/**
 * @Classname LoginTypeDeserializer
 * @Description
 * @Date 2022/3/31 10:41 上午
 * @Created by gz-d
 */
public class LoginTypeDeserializer extends JsonDeserializer<LoginTypeEnum> {
    @Override
    public LoginTypeEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        LoginTypeEnum type = LoginTypeEnum.getByType(value);
        if (null == type) {
            throw new IllegalArgumentException(String.format("Valid code %s type is not supported", value));
        }
        return type;
    }

    @Override
    public LoginTypeEnum getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return super.getNullValue(ctxt);
    }
}
