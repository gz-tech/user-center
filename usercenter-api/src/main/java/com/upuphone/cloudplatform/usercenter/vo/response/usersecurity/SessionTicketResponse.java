package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionTicketResponse {

    private String sessionTicket;
}
