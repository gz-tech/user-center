package com.upuphone.cloudplatform.usercenter.service.open;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserInfoRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserInfoResponse;
import com.upuphone.cloudplatform.usercenter.api.vo.UserInfoVo;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.OrikaUtil;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserInfoService extends BaseService<UserInfoRequest, UserInfoResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    protected void validate(UserInfoRequest userInfoRequest) {

    }

    @Override
    protected UserInfoResponse processCore(UserInfoRequest userInfoRequest) throws Exception {
        Set<Long> userIds = userInfoRequest.getUserIds().stream().filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) {
            return new UserInfoResponse().setUserInfoList(new ArrayList<>());
        }
        List<UserBaseInfoPo> pos = userBaseInfoMapper.selectBatchIds(userIds);
        if (CollectionUtils.isEmpty(pos)) {
            return new UserInfoResponse().setUserInfoList(new ArrayList<>());
        }
        List<UserInfoVo> vos = pos.stream().map(po -> {
            UserInfoVo vo = OrikaUtil.map(po, UserInfoVo.class);
            vo.setUserId(po.getId());
            vo.setPhoneNumberMask(MaskUtil.maskPhone(po.getPhoneNumber()));
            vo.setSecurityPhoneNumberMask(MaskUtil.maskPhone(po.getSecurityPhoneNumber()));
            vo.setEmailMask(MaskUtil.maskEmail(po.getEmail()));
            return vo;
        }).collect(Collectors.toList());
        return new UserInfoResponse().setUserInfoList(vos);
    }
}
