package com.upuphone.cloudplatform.usercenter.remote.email.model;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Data
@Builder
public class SendEmailRequest {

    /**
     * 要发送的邮箱（支持逗号隔开）
     */
    private String to;

    /**
     * 主题
     */
    private String subject;

    /**
     * 抄送
     */
    private String cc;

    /**
     * 密送
     */
    private String bcc;

    /**
     * 正文 若context不为null将忽略
     */
    private String text;

    /**
     * 附件
     */
    private List<EmailAttachment> attachmentList;

    /**
     * 发送日期 默认current
     */
    private Date sentDate;

    /**
     * thymeleaf正文 仅支持html
     */
    private Context context;
}
