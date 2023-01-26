package com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.constants.ThirdPartyAccountEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountBindingListRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingListResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountBindingListService extends BaseService<AccountBindingListRequest, AccountBindingListResponse> {

    @Autowired
    private UserThirdPartyAccountMapper accountMapper;

    @Override
    protected void validate(AccountBindingListRequest accountBindingListRequest) {
        if (null == RequestContext.getUserId()) {
            throw new BusinessException(UserCenterErrorCode.NOT_LOGIN_ERROR);
        }
    }

    @Override
    protected AccountBindingListResponse processCore(AccountBindingListRequest accountBindingListRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        Set<Integer> allTypes = ThirdPartyAccountEnum.getAllTypes();
        List<AccountBindingListResponse.Binding> bindings = new ArrayList<>();
        allTypes.forEach(type -> {
            AccountBindingListResponse.Binding binding = new AccountBindingListResponse.Binding();
            binding.setBoundType(type);
            binding.setBoundName(ThirdPartyAccountEnum.getByType(type).getName());
            binding.setIsBounded(false);
            bindings.add(binding);
        });
        AccountBindingListResponse response = new AccountBindingListResponse();
        response.setBindings(bindings);
        List<UserThirdPartyAccountPo> poList = accountMapper.selectList(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                .eq(UserThirdPartyAccountPo::getUserId, userId));
        if (CollectionUtils.isEmpty(poList)) {
            return response;
        }
        Map<Integer, UserThirdPartyAccountPo> typeMap = poList.stream()
                .filter(o -> null != ThirdPartyAccountEnum.getByType(o.getType()))
                .collect(Collectors.toMap(UserThirdPartyAccountPo::getType, v -> v, (v1, v2) -> v1));
        bindings.forEach(b -> {
            UserThirdPartyAccountPo po = typeMap.get(b.getBoundType());
            if (null != po) {
                b.setBoundId(po.getId().toString());
                b.setIsBounded(true);
            }
        });
        return response;
    }
}
