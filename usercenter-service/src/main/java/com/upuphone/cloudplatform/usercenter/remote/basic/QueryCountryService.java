package com.upuphone.cloudplatform.usercenter.remote.basic;

import com.upuphone.cloudplatform.baseinfo.api.geo.GeoClient;
import com.upuphone.cloudplatform.baseinfo.vo.country.request.QueryCountryRequest;
import com.upuphone.cloudplatform.baseinfo.vo.country.response.QueryCountryResponse;
import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.exception.RemoteException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueryCountryService extends BaseRemoteService<QueryCountryRequest, QueryCountryResponse, CommonResponse<QueryCountryResponse>> {
    @Autowired
    private GeoClient geoClient;

    public QueryCountryService(@Value("baseinfo") String service, @Value("geo-country") String apiName) {
        super(service, apiName);
    }

    @Override
    protected QueryCountryResponse fromRemoteResponse(CommonResponse<QueryCountryResponse> response) {
        if (response == null
                || response.getCode() != 0 || response.getData() == null) {
            throw new RemoteException(CommonErrorCode.REMOTE_ERROR);
        }
        return response.getData();
    }

    @Override
    protected CommonResponse<QueryCountryResponse> processCore(QueryCountryRequest request) throws Exception {
        return geoClient.queryCountryList(request);
    }

    @Override
    protected String getServiceName() {
        return "baseinfo";
    }

    @Override
    protected String getApiName() {
        return "geo-country";
    }
}
