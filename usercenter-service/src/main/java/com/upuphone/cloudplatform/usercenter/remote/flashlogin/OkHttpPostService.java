package com.upuphone.cloudplatform.usercenter.remote.flashlogin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@SuppressWarnings("unchecked")
public class OkHttpPostService {

    @Autowired
    private OkHttpClient okHttpClient;

    /**
     * token置换手机号后台接口
     */
    public <T, CP> T postRequest(String url, Map<String, String> params, CP cp) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> m : params.entrySet()) {
                builder.add(m.getKey(), m.getValue());
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder().post(body).url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && null != response.body()) {
                String content = response.body().string();
                if (StringUtils.isNotBlank(content)) {
                    if (cp instanceof TypeReference) {
                        return JsonUtility.getObjectMapper().readValue(content, (TypeReference<? extends T>) cp);
                    } else if (cp instanceof Class) {
                        return JsonUtility.toObject(content, (Class<? extends T>) cp);
                    }
                    throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "invalid type");
                }
            }
        } catch (Exception e) {
            log.error("请求闪验服务器失败, e=", e);
            throw new RuntimeException(e);
        }
        return null;
    }
}
