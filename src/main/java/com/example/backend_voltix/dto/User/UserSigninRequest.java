package com.example.backend_voltix.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSigninRequest {
    private String usernameOrEmail;
    private String password;
}
