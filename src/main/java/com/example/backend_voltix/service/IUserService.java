package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.User.*;

public interface IUserService {
    UserSignupResponse signupUser(UserSignupRequest userSignupRequest);
    UserSignupResponse getUserDetailsById(Long id);
    UserSigninResponse signIn(UserSigninRequest userSigninRequest);
    PasswordResetResponse resetPassword(PasswordResetRequest passwordResetRequest);
    UserProfileUpdateResponse updateUserProfile(String username, UserProfileUpdateRequest userProfileUpdateRequest);
}
