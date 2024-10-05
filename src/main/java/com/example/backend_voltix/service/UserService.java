package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.User.*;
import com.example.backend_voltix.model.Client;
import com.example.backend_voltix.model.Device;
import com.example.backend_voltix.model.Phones;
import com.example.backend_voltix.model.User;
import com.example.backend_voltix.repository.ClientRepository;
import com.example.backend_voltix.repository.DeviceRepository;
import com.example.backend_voltix.repository.PhoneRepository;
import com.example.backend_voltix.repository.UserRepository;
import com.example.backend_voltix.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${upload.path}"+"userImage")
    private String UPLOAD_DIR ;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PhoneRepository phoneRepository;

    @Transactional
    public UserSignupResponse signupUser(UserSignupRequest userSignupRequest) {
        logger.info("Starting signup process for user: {}", userSignupRequest.getUsername());

        // Check if device exists
        logger.info("Looking for device with serial number: {}", userSignupRequest.getSerialNumber());
        Optional<Device> deviceSN = deviceRepository.findDeviceBySerialNumber(userSignupRequest.getSerialNumber());
        logger.info("Query executed: {}", deviceSN.isPresent() ? "Device found" : "Device not found");

        if (deviceSN.isEmpty()) {
            logger.error("Device with serial number {} not found", userSignupRequest.getSerialNumber());
            throw new RuntimeException("Device with serial number " + userSignupRequest.getSerialNumber() + " not found");
        }

        Device device = deviceSN.get();

        // Check if device is already assigned to a client
        if (device.getClient() != null) {
            logger.error("Device with serial number {} is already assigned to a client", userSignupRequest.getSerialNumber());
            throw new RuntimeException("Device with serial number " + userSignupRequest.getSerialNumber() + " is already assigned to a client");
        }

        logger.info("Device with serial number {} found", userSignupRequest.getSerialNumber());

        // Create the user
        User user = new User();
        user.setFirstname(userSignupRequest.getFirstname());
        user.setLastname(userSignupRequest.getLastname());
        user.setEmail(userSignupRequest.getEmail());
        user.setUsername(userSignupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword()));
        user.setRole(userSignupRequest.getRole());
        user.setEnable(true);
        userRepository.save(user);
        logger.info("User {} created successfully", user.getUsername());

        // Create the client and associate the user
        Client client = new Client();
        client.setName(userSignupRequest.getFirstname() + " " + userSignupRequest.getLastname());
        clientRepository.save(client);
        logger.info("Client {} created successfully", client.getName());

        // Assign the user to the client
        user.setClient(client);
        userRepository.save(user);

        // Assign the device to the client
        device.setClient(client);
        deviceRepository.save(device);
        logger.info("Device {} assigned to client {}", device.getSerialNumber(), client.getName());

        // Create and return the response
        UserSignupResponse response = new UserSignupResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                device.getSerialNumber()
        );

        return response;
    }

    public UserSignupResponse getUserDetailsById(Long id) {
        logger.info("Fetching details for user with username: {}", id);

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            logger.error("User with username {} not found", id);
            throw new RuntimeException("User with username " + id + " not found");
        }

        User user = userOptional.get();
        Device device = deviceRepository.findByClientId(user.getClient().getId())
                .orElseThrow(() -> new RuntimeException("No device found for client with ID " + user.getClient().getId()));

        return new UserSignupResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                device.getSerialNumber()
        );
    }

    @Override
    public UserSigninResponse signIn(UserSigninRequest userSigninRequest) {
        logger.info("Starting sign-in process for user: {}", userSigninRequest.getUsernameOrEmail());

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(userSigninRequest.getUsernameOrEmail());
        if (!optionalUser.isPresent()) {
            logger.error("User with username or email {} not found", userSigninRequest.getUsernameOrEmail());
            throw new RuntimeException("User with username or email " + userSigninRequest.getUsernameOrEmail() + " not found");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(userSigninRequest.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user {}", userSigninRequest.getUsernameOrEmail());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User {} signed in successfully", user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername());
        List<String> phoneNumbers = user.getPhones().stream()
                .map(phone -> phone.getNumber())
                .collect(Collectors.toList());
        // User has Only one Device
        String deviceSerialnbr  = user.getClient().getDevices().get(0).getSerialNumber();
        String deviceName  = user.getClient().getDevices().get(0).getDeviceName();

        return new UserSigninResponse(user.getId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail(), token,phoneNumbers,user.getImageUrl(),deviceSerialnbr,deviceName);

    }


    @Override
    public PasswordResetResponse resetPassword(PasswordResetRequest passwordResetRequest) {
        logger.info("Starting password reset process for email: {}", passwordResetRequest.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(passwordResetRequest.getEmail());
        if (userOptional.isEmpty()) {
            logger.error("User with email {} not found", passwordResetRequest.getEmail());
            throw new RuntimeException("User with email " + passwordResetRequest.getEmail() + " not found");
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);

        logger.info("Password reset successfully for user {}", user.getUsername());
        return new PasswordResetResponse("Password reset successfully");
    }



    @Transactional
    public UserProfileUpdateResponse updateUserProfile(String username, UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User with username " + username + " not found");
        }

        updateUserInfo(user, userProfileUpdateRequest);
        handleImageUpload(user, userProfileUpdateRequest.getImage());
        userRepository.save(user);

        List<String> phoneNumbers = user.getPhones().stream()
                .map(Phones::getNumber)
                .collect(Collectors.toList());

        return new UserProfileUpdateResponse(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getUsername(), phoneNumbers, user.getImageUrl());
    }

    private void updateUserInfo(User user, UserProfileUpdateRequest userProfileUpdateRequest) {
        user.setFirstname(userProfileUpdateRequest.getFirstname());
        user.setLastname(userProfileUpdateRequest.getLastname());
        user.setEmail(userProfileUpdateRequest.getEmail());

        if (userProfileUpdateRequest.getPhoneNumbers() != null) {

            phoneRepository.deleteAllByUserId(user.getId());

            List<String> newPhoneNumbers = userProfileUpdateRequest.getPhoneNumbers().stream()
                    .distinct() // Remove duplicates in the incoming phone numbers
                    .limit(3) // Limit to 3 phone numbers
                    .collect(Collectors.toList());

            if (newPhoneNumbers.size() > 3) {
                throw new RuntimeException("Cannot have more than 3 phone numbers.");
            }

            // Clear all current phone numbers for the user
            user.getPhones().clear();

            // Add new phone numbers
            for (String newPhoneNumber : newPhoneNumbers) {
                user.getPhones().add(new Phones(null, newPhoneNumber, user));
            }

            // Ensure the user's phone list is updated
            user.setPhones(user.getPhones());
        }

    }

    private void handleImageUpload(User user, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = saveImageFile(user.getId(), imageFile);
                user.setImageUrl("/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image file", e);
            }
        }
    }

    private String saveImageFile(Long userId, MultipartFile imageFile) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String fileName = userId + "_" + timestamp + "_" + imageFile.getOriginalFilename().replaceAll(" ", "_");
        Path filePath = Paths.get(UPLOAD_DIR +"/"+ fileName);
        Files.write(filePath, imageFile.getBytes());
        return fileName;
    }
}




