package com.example.backend_voltix.dto.User;

import com.example.backend_voltix.model.Role;
import lombok.Data;

@Data
public class UserSignupRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;

    private String serialNumber;

    public Role getRole() {
        return Role.USER;
    }
}
