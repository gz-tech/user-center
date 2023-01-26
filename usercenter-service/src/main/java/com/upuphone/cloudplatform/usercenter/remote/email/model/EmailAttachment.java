package com.upuphone.cloudplatform.usercenter.remote.email.model;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Data
public class EmailAttachment {

    private String attachmentName;

    private InputStreamSource inputStreamSource;
}
