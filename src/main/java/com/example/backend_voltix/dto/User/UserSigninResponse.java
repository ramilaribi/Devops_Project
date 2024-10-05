package com.example.backend_voltix.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserSigninResponse {
  private Long userId;
  private String firstname;
  private String lastname;
  private String username;
  private String email;
  private String token;

  private List<String> phoneNumbers;
  private String image;
  private String deviceSerialNbr;
  private String deviceName;
}
