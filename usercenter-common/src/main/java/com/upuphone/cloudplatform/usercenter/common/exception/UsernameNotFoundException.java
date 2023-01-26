package com.upuphone.cloudplatform.usercenter.common.exception;

/**
 * @author guangzheng.ding
 * @date 2021/12/13 14:51
 */
public class UsernameNotFoundException extends RuntimeException {

    public UsernameNotFoundException() {
        super();
    }

    public UsernameNotFoundException(String message) {
        super(message);
    }
}
