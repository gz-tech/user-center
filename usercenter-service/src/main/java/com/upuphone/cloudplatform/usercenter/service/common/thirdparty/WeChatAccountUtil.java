package com.upuphone.cloudplatform.usercenter.service.common.thirdparty;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.storage.api.dto.storage.response.StorageInfoUrlRspDTO;
import com.upuphone.cloudplatform.usercenter.remote.storage.FileUploadRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.wechat.token.WeChatTokenRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.wechat.token.model.WeChatTokenRequest;
import com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.WeChatUserInfoRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.model.WeChatUserInfoRequest;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.WeChatTokenBo;
import com.upuphone.cloudplatform.usercenter.service.util.FileUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@Service
public class WeChatAccountUtil {

    @Autowired
    private WeChatTokenRemoteService weChatTokenRemoteService;
    @Autowired
    private WeChatUserInfoRemoteService weChatUserInfoRemoteService;
    @Autowired
    private FileUploadRemoteService fileUploadRetryRemoteService;

    /**
     * 根据授权码获取accessToken&openId
     *
     * @param code 授权码
     * @return WeChatTokenBo
     */
    public WeChatTokenBo getAccessTokenAndOpenIdByCode(String code) {
        WeChatTokenRequest request = new WeChatTokenRequest();
        request.setCode(code);
        return weChatTokenRemoteService.process(request);
    }

    /**
     * 根据accessToken和userId获取第三方用户信息
     *
     * @param accessToken accessToken
     * @param openId      openId
     * @return 第三方用户信息
     */
    public UserThirdAccountBaseInfo getUserInfo(String accessToken, String openId) {
        WeChatUserInfoRequest request = new WeChatUserInfoRequest();
        request.setAccessToken(accessToken);
        request.setOpenId(openId);
        return weChatUserInfoRemoteService.process(request);
    }

    /**
     * 根据头像url上传头像并获取链接
     *
     * @param headImgUrl 头像URL
     * @return 网盘链接
     */
    @SneakyThrows
    public String uploadHeadImgAndGetUrl(String headImgUrl) {
        if (StringUtils.isBlank(headImgUrl)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "头像URL不能为空");
        }
        // File转换成MultipartFile
        File file = FileUtil.createFileByUrl(headImgUrl, "jpeg");
        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), null, inputStream);
        StorageInfoUrlRspDTO resp = fileUploadRetryRemoteService.process(multipartFile);
        return resp.getUrl();
    }

    @SneakyThrows
    public String uploadWechatPhoto(String headImgUrl) {
        if (StringUtils.isBlank(headImgUrl)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "头像URL不能为空");
        }
        MultipartFile multipartFile = UrlToMultipartFile.urlToMultipartFile(headImgUrl);
        StorageInfoUrlRspDTO resp = fileUploadRetryRemoteService.process(multipartFile);
        return resp.getUrl();
    }
}
