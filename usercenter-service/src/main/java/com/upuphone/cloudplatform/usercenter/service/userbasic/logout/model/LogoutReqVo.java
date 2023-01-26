package com.upuphone.cloudplatform.usercenter.service.userbasic.logout.model;

import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class LogoutReqVo {

    private String password;

    private LogoutType logoutType;


    @Getter
    public enum LogoutType {
        // regist by phone
        APP(1),
        // regist by email
        WEB(2),
        HELLAS(3);

        private final int type;

        LogoutType(int type) {
            this.type = type;
        }

        public static RegistReqBo.RegistType getByType(Integer type) {
            for (RegistReqBo.RegistType registType : RegistReqBo.RegistType.values()) {
                if (Objects.equals(type, registType.getType())) {
                    return registType;
                }
            }
            return null;
        }
    }
}
