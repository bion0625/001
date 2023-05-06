package com.uj.stxtory.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginUser {
    private String id;
    private String name;
    private String loginCheck;

    public LoginUser(String userName, String loginMsg) {
        name = userName;
        loginCheck = loginMsg;
    }
}
