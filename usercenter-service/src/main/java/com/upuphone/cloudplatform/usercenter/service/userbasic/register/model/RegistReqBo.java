package com.upuphone.cloudplatform.usercenter.service.userbasic.register.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class RegistReqBo {
    private String phoneCode;

    private String phoneNumber;

    private String emailAddress;

    private String password;

    private String ticket;

    private RegistType registType;

    private String thirdPartAuthTicket;

    @Getter
    public enum RegistType {
        // regist by phone
        PHONE(1),
        // regist by email
        EMAIL(2);
        private final int type;

        RegistType(int type) {
            this.type = type;
        }

        public static RegistType getByType(Integer type) {
            for (RegistType registType : RegistType.values()) {
                if (Objects.equals(type, registType.getType())) {
                    return registType;
                }
            }
            return null;
        }
    }
}
