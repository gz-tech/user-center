package com.upuphone.cloudplatform.usercenter.remote.email;

import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.remote.email.model.EmailAttachment;
import com.upuphone.cloudplatform.usercenter.remote.email.model.SendEmailRequest;
import com.upuphone.cloudplatform.usercenter.remote.email.model.SendEmailResponse;
import com.upuphone.cloudplatform.usercenter.remote.email.util.EmailValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Service
@Slf4j
public class SendEmailRemoteService extends BaseRemoteService<SendEmailRequest, SendEmailResponse, Void> {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String from;

    public SendEmailRemoteService(@Value("email") String service, @Value("send") String apiName) {
        super(service, apiName);
    }

    @Override
    protected SendEmailResponse fromRemoteResponse(Void unused) {
        return SendEmailResponse.builder().status(true).build();
    }

    @Override
    protected Void processCore(SendEmailRequest sendEmailRequest) throws Exception {
        checkParam(sendEmailRequest);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        boolean isMulti = null != sendEmailRequest.getContext() || CollectionUtils.isNotEmpty(sendEmailRequest.getAttachmentList());
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isMulti);
        helper.setSubject(sendEmailRequest.getSubject());
        helper.setFrom(from);
        helper.setTo(sendEmailRequest.getTo());
        if (StringUtils.isNotBlank(sendEmailRequest.getCc())) {
            helper.setCc(sendEmailRequest.getCc());
        }
        if (StringUtils.isNotBlank(sendEmailRequest.getBcc())) {
            helper.setBcc(sendEmailRequest.getBcc());
        }
        if (StringUtils.isNotBlank(sendEmailRequest.getText())) {
            helper.setText(sendEmailRequest.getText());
        }
        helper.setSentDate(null == sendEmailRequest.getSentDate() ? new Date() : sendEmailRequest.getSentDate());
        if (isMulti) {
            if (CollectionUtils.isNotEmpty(sendEmailRequest.getAttachmentList())) {
                for (EmailAttachment emailAttachment : sendEmailRequest.getAttachmentList()) {
                    helper.addAttachment(emailAttachment.getAttachmentName(), emailAttachment.getInputStreamSource());
                }
            }
            if (null != sendEmailRequest.getContext()) {
                helper.setText(templateEngine.process("template.html", sendEmailRequest.getContext()), true);
            }
        }
        javaMailSender.send(mimeMessage);
        return null;
    }

    @Override
    protected String getServiceName() {
        return "email";
    }

    @Override
    protected String getApiName() {
        return "send";
    }

    private void checkParam(SendEmailRequest sendEmailRequest) {
        if (StringUtils.isBlank(sendEmailRequest.getSubject())) {
            log.error("subject must not be null!");
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isBlank(sendEmailRequest.getTo())
                || !EmailValidation.patternMatches(sendEmailRequest.getTo())) {
            log.error("invalid to:[{}]", sendEmailRequest.getTo());
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isNotBlank(sendEmailRequest.getCc())
                && !EmailValidation.patternMatches(sendEmailRequest.getCc())) {
            log.error("invalid cc:[{}]", sendEmailRequest.getCc());
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isNotBlank(sendEmailRequest.getBcc())
                && !EmailValidation.patternMatches(sendEmailRequest.getBcc())) {
            log.error("invalid bcc:[{}]", sendEmailRequest.getBcc());
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isBlank(sendEmailRequest.getText())
                && null == sendEmailRequest.getContext()
                && CollectionUtils.isEmpty(sendEmailRequest.getAttachmentList())) {
            log.error("text && context && attachment must not be null!");
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
    }
}
