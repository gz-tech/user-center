package com.upuphone.cloudplatform.usercenter.service.basic;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.storage.api.dto.storage.response.StorageInfoUrlRspDTO;
import com.upuphone.cloudplatform.usercenter.remote.storage.FileUploadRemoteService;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.UpLoadPhotoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
@Service
@Slf4j
public class UploadPhotoService extends BaseService<MultipartFile, UpLoadPhotoResponse> {

    @Autowired
    private FileUploadRemoteService fileUploadRetryRemoteService;


    @Override
    protected void validate(MultipartFile request) {

        if (Strings.isNullOrEmpty(RequestContext.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id cannot null");
        }
    }

    @Override
    protected UpLoadPhotoResponse processCore(MultipartFile soaRequest) throws Exception {

        StorageInfoUrlRspDTO storageResult = fileUploadRetryRemoteService.process(soaRequest);
        UpLoadPhotoResponse response = new UpLoadPhotoResponse();
        response.setFileId(storageResult.getFileId());
        response.setUrl(storageResult.getUrl());
        return response;
    }
}
