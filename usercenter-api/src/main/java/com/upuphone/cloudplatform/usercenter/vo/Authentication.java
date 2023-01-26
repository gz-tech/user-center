package com.upuphone.cloudplatform.usercenter.vo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * mark the interface need login status
 *
 * @author ryan
 */
@Target(ElementType.METHOD)
public @interface Authentication {

}
