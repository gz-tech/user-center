package com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;

import java.io.IOException;

public class ValidCodeTypeDeserializer extends JsonDeserializer<ValidCodeType> {
    @Override
    public ValidCodeType deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        int text = jsonParser.getIntValue();
        ValidCodeType type = ValidCodeType.getByType(text);
        if (null == type) {
            throw new IllegalArgumentException(String.format("Valid code %s type is not supported", text));
        }
        return type;
    }
}
