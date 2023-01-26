package com.upuphone.cloudplatform.usercenter.remote.storage;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.storage.api.dto.storage.response.StorageInfoUrlRspDTO;
import com.upuphone.cloudplatform.usercenter.remote.RetryRemoteService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("fileUploadRetryRemoteService")
public class FileUploadRetryRemoteService extends FileUploadRemoteService implements RetryRemoteService {


    public FileUploadRetryRemoteService(@Value("cloud-storage") String service,
            @Value("createObject-retry") String apiName) {
        super(service, apiName);
    }


    @Override
    @Retryable(include = {FeignException.class}, maxAttempts = 3,
            backoff = @Backoff(value = 2000, multiplier = 1.5))
    protected CommonResponse<StorageInfoUrlRspDTO> processCore(MultipartFile file) throws Exception {
        return super.processCore(file);
    }

    @Override
    protected String getApiName() {
        return "createObject-retry";
    }
}
