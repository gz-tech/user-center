package com.upuphone.cloudplatform.usercenter.service.hellas;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.api.constant.RemoteSourceEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.WeChatAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.hellas.util.CheckTokenUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.CreateAccountIfAbsentRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.CreateAccountIfAbsentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateLotusAccountService extends BaseService<CreateAccountIfAbsentRequest, CreateAccountIfAbsentResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private CheckTokenUtil checkTokenUtil;
    @Autowired
    private WeChatAccountUtil weChatAccountUtil;

    @Override
    protected void validate(CreateAccountIfAbsentRequest createAccountIfAbsentRequest) {

    }

    @Override
    protected CreateAccountIfAbsentResponse processCore(CreateAccountIfAbsentRequest request) throws Exception {
        checkTokenUtil.checkToken(request.getLotusAccessToken(), request.getLotusId(), request.getMobile(), RemoteSourceEnum.LOTUS);
        UserBaseInfoPo existOne = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getPhoneNumber, request.getMobile())
                .eq(UserBaseInfoPo::getPhoneCode, "0086"));
        if (existOne != null) {
            return new CreateAccountIfAbsentResponse(existOne.getId().toString());
        } else {
            UserBaseInfoPo insertOne = new UserBaseInfoPo();
            insertOne.setUserName(request.getUserInfo().getNickName());
            try {
                String phoneUrlNew = weChatAccountUtil.uploadHeadImgAndGetUrl(request.getUserInfo().getAvatar());
                insertOne.setPhotoUrl(phoneUrlNew);
            } catch (Exception e) {
                insertOne.setPhotoUrl(request.getUserInfo().getAvatar());
            }
            insertOne.setPhoneNumber(request.getMobile());
            //暂时只考虑国内需求 没有传号码code
            insertOne.setPhoneCode("0086");
            userBaseInfoMapper.insert(insertOne);
            return new CreateAccountIfAbsentResponse(insertOne.getId().toString());
        }
    }
}
