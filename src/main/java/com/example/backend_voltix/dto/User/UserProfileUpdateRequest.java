package com.example.backend_voltix.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    private String firstname;
    private String lastname;
    private String email;
    private List<String> phoneNumbers;
    private MultipartFile image;

}
