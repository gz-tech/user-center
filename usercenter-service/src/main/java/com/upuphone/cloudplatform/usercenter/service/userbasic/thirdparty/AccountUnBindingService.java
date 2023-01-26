package com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountUnbindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountUnbindingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountUnBindingService extends BaseService<AccountUnbindingRequest, AccountUnbindingResponse> {

    @Autowired
    private UserThirdPartyAccountMapper userThirdPartyAccountMapper;

    @Override
    protected void validate(AccountUnbindingRequest accountUnbindingRequest) {
        if (RequestContext.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "未查询到登录态");
        }
    }

    @Override
    protected AccountUnbindingResponse processCore(AccountUnbindingRequest accountUnbindingRequest) throws Exception {

        UserThirdPartyAccountPo userThirdPartyAccountPo = userThirdPartyAccountMapper.selectOne(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                .eq(UserThirdPartyAccountPo::getUserId, RequestContext.getUserId())
                .eq(UserThirdPartyAccountPo::getType, accountUnbindingRequest.getBingdingType()));

        if (userThirdPartyAccountPo == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "绑定关系不存在");
        }

        int status = userThirdPartyAccountMapper.deleteById(userThirdPartyAccountPo.getId());

        if (status == 0) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "解绑失败");
        }

        return new AccountUnbindingResponse();
    }
}
