package com.upuphone.cloudplatform.usercenter.service.userbasic.register.util;

import com.upuphone.cloudplatform.common.utils.DateTimeUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.TicketContext;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TicketUtil {
    @Autowired
    private RegisterSetting registerSetting;


    public final String generateTicket(String deviceId, String phoneNumber, String emailAddress, String validCode) {
        Map<String, Object> payloadParams = new HashMap<>();
        payloadParams.put("deviceId", deviceId);
        payloadParams.put("phoneNumber", phoneNumber);
        payloadParams.put("emailAddress", emailAddress);
        payloadParams.put("validCode", validCode);
        Date expiredDate = DateTimeUtil.addSeconds(new Date(), registerSetting.getRegistTicketValidDuration());
        String token = Jwts.builder().setSubject("checkRegisterValid")
                .setExpiration(expiredDate).addClaims(payloadParams)
                .signWith(SignatureAlgorithm.HS512, registerSetting.getSignKey()).compact();
        return token;
    }

    public final TicketContext parseTicket(String ticket) {
        Claims payloads = Jwts.parser()
                .setSigningKey(registerSetting.getSignKey())
                .parseClaimsJws(ticket).getBody();

        TicketContext ticketContext = new TicketContext();
        ticketContext.setDeviceId((String) payloads.get("deviceId"));
        ticketContext.setPhoneNumber((String) payloads.get("phoneNumber"));
        ticketContext.setEmailAddress((String) payloads.get("emailAddress"));
        ticketContext.setValidCode((String) payloads.get("validCode"));
        ticketContext.setExpiredDate(payloads.getExpiration());
        return ticketContext;
    }
}
