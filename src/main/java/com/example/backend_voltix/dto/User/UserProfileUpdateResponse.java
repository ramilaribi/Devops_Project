package com.example.backend_voltix.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileUpdateResponse {
    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private List<String> phoneNumbers;
    private String image;
}
