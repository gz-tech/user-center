package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.RelationDeviceRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.RelationDeviceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/28
 */
@Slf4j
@Service
public class RelationDeviceService extends BaseService<RelationDeviceRequest, RelationDeviceResponse> {

    @Override
    protected void validate(RelationDeviceRequest request) {

    }

    @Override
    protected RelationDeviceResponse processCore(RelationDeviceRequest soaRequest) throws Exception {
        return null;
    }
}
