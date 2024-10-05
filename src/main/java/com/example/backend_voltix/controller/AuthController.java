package com.example.backend_voltix.controller;

import com.example.backend_voltix.dto.User.*;
import com.example.backend_voltix.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "User Signup", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed up successfully"),
            @ApiResponse(responseCode = "404", description = "User signup failed, resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<Object> signupUser(
            @Parameter(description = "User signup request containing user information", required = true)
            @RequestBody UserSignupRequest userSignupRequest,
            HttpServletRequest request) {
        logger.info("Received signup request from IP: {}", request.getRemoteAddr());
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Method: {}", request.getMethod());

        try {
            UserSignupResponse response = userService.signupUser(userSignupRequest);
            logger.info("User {} signed up successfully", userSignupRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error during signup process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during signup process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get User Details", description = "Fetches user details by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/details")
    public UserSignupResponse getUserDetails(
            @Parameter(description = "User details request containing user ID", required = true)
            @RequestBody UserDetailsRequest request) {
        return userService.getUserDetailsById(request.getUserId());
    }

    @Operation(summary = "User Signin", description = "Authenticates a user and returns a token if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Parameter(description = "User signin request containing credentials", required = true)
            @RequestBody UserSigninRequest userSigninRequest) {
        try {
            UserSigninResponse response = userService.signIn(userSigninRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Operation(summary = "Reset Password", description = "Allows a user to reset their password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password reset request")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @Parameter(description = "Password reset request containing user email or username", required = true)
            @RequestBody PasswordResetRequest passwordResetRequest) {
        PasswordResetResponse response = userService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update User Profile", description = "Updates user profile information including image upload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update-profile/{username}")
    public ResponseEntity<UserProfileUpdateResponse> updateUserProfile(
            @Parameter(description = "Username of the user whose profile needs to be updated", required = true)
            @PathVariable String username,
            @Parameter(description = "First name", required = true) @RequestParam("firstname") String firstname,
            @Parameter(description = "Last name", required = true) @RequestParam("lastname") String lastname,
            @Parameter(description = "Email", required = true) @RequestParam("email") String email,
            @Parameter(description = "Phone numbers as a map", required = true) @RequestParam Map<String, String> allParams,
            @Parameter(description = "Profile image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        List<String> phoneNumbers = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("phoneNumbers["))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        userProfileUpdateRequest.setFirstname(firstname);
        userProfileUpdateRequest.setLastname(lastname);
        userProfileUpdateRequest.setEmail(email);
        userProfileUpdateRequest.setPhoneNumbers(phoneNumbers);
        userProfileUpdateRequest.setImage(image);
        UserProfileUpdateResponse response = userService.updateUserProfile(username, userProfileUpdateRequest);
        return ResponseEntity.ok(response);
    }
}
