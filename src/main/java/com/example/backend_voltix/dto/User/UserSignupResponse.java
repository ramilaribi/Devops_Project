package com.example.backend_voltix.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSignupResponse {
  private Long userId;
  private String firstname;
  private String lastname;
  private String deviceSerialNumber;
}
