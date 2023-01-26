package com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;

import java.io.IOException;

/**
 * @Classname CaptchaBusinessTypeDeserializer
 * @Description TODO
 * @Date 2022/6/1 11:07 上午
 * @Created by gz-d
 */
public class CaptchaBusinessTypeDeserializer extends JsonDeserializer<CaptchaBusinessEnum> {
    @Override
    public CaptchaBusinessEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        CaptchaBusinessEnum type = CaptchaBusinessEnum.getByType(value);
        if (null == type) {
            throw new IllegalArgumentException(String.format("Valid code %s type is not supported", value));
        }
        return type;
    }

    @Override
    public CaptchaBusinessEnum getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return super.getNullValue(ctxt);
    }
}
