package com.upuphone.cloudplatform.usercenter.service.hellas;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.api.constant.RemoteSourceEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.hellas.util.CheckTokenUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.CheckUserExistsByMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.CheckUserExistsByMobileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckUserExistsByMobileService extends BaseService<CheckUserExistsByMobileRequest, CheckUserExistsByMobileResponse> {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private CheckTokenUtil checkTokenUtil;

    @Override
    protected void validate(CheckUserExistsByMobileRequest checkUserExistsByMobileRequest) {

    }

    @Override
    protected CheckUserExistsByMobileResponse processCore(CheckUserExistsByMobileRequest request) throws Exception {
        checkTokenUtil.checkToken(request.getLotusAccessToken(), request.getLotusId(), request.getMobile(), RemoteSourceEnum.LOTUS);
        UserBaseInfoPo existUser = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getPhoneNumber, request.getMobile())
                .eq(UserBaseInfoPo::getPhoneCode, "0086"));
        CheckUserExistsByMobileResponse result = new CheckUserExistsByMobileResponse();
        if (existUser != null) {
            result.setResult(true);
            result.setUserId(existUser.getId().toString());
        } else {
            result.setResult(false);
        }
        return result;
    }
}
