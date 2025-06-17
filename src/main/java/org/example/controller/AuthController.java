//package org.example.controller;
//
//import org.example.dto.LoginRequest;
//import org.example.dto.SignupRequest;
//import org.example.dto.TokenResponse;
//import org.example.entity.User;
//import org.example.model.NotificationEvent;
//import org.example.service.AuthService;
//import org.example.repository.UserRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    private final AuthService authService;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public AuthController(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder, KafkaTemplate<String, Object> kafkaTemplate) {
//        this.authService = authService;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> signUp(@RequestBody SignupRequest request) {
//        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//
//        User user = new User(
//                null,
//                request.getUsername(),
//                passwordEncoder.encode(request.getPassword()),
//                "ROLE_EMPLOYEE"
//        );
//
//        try {
//            User savedUser = userRepository.save(user);
//
//            // Send welcome notification
//            try {
//                NotificationEvent event = new NotificationEvent(
//                    "USER_REGISTERED",
//                    savedUser.getId(),
//                    savedUser.getUsername(), // Using username as email since email field is not in User entity
//                    "Welcome to TaskApp",
//                    "Welcome to TaskApp! Your account has been successfully created."
//                );
//
//                kafkaTemplate.send("user-events", event);
//            } catch (Exception e) {
//                // Log error but don't fail registration
//                System.err.println("Failed to send welcome notification: " + e.getMessage());
//            }
//
//            return ResponseEntity.status(201).body("User registered successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            String token = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
//            User user = userRepository.findByUsername(request.getUsername())
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//            TokenResponse response = new TokenResponse(
//                    token,
//                    user.getId(),
//                    user.getUsername(),
//                    user.getRole()
//            );
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
//        }
//    }
//}




package org.example.controller;

import org.example.dto.LoginRequest;
import org.example.dto.SignupRequest;
import org.example.dto.TokenResponse;
import org.example.entity.User;
import org.example.entity.EmployeeEntity;
import org.example.model.NotificationEvent;
import org.example.service.AuthService;
import org.example.repository.UserRepository;
import org.example.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> signUp(@RequestBody SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_EMPLOYEE"
        );

        try {
            // Save user first
            User savedUser = userRepository.save(user);

            // If user has ROLE_EMPLOYEE, create employee record
            if ("ROLE_EMPLOYEE".equals(savedUser.getRole())) {
                createEmployeeRecord(savedUser);
            }

            // Send welcome notification
            try {
                NotificationEvent event = new NotificationEvent(
                        "USER_REGISTERED",
                        savedUser.getId(),
                        savedUser.getUsername(),
                        "Welcome to TaskApp",
                        "Welcome to TaskApp! Your account has been successfully created."
                );

                kafkaTemplate.send("user-events", event);
            } catch (Exception e) {
                System.err.println("Failed to send welcome notification: " + e.getMessage());
            }

            return ResponseEntity.status(201).body(
                    "User registered successfully. Employee record created with ID: " + savedUser.getId()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
        }
    }

    private void createEmployeeRecord(User user) {
        try {
            // Check if employee already exists
            if (employeeRepository.findById(user.getId()).isPresent()) {
                System.out.println("Employee record already exists for user: " + user.getUsername());
                return;
            }

            // Create new employee record
            EmployeeEntity employee = new EmployeeEntity();
            employee.setId(user.getId()); // Same ID as user
            employee.setFirstname(user.getUsername());
            employee.setLastname("");
            employee.setEmail(user.getUsername() + "@company.com");
            employee.setDepartmentId("DEFAULT");
            employee.setRoleId("21");
            employeeRepository.save(employee);
            System.out.println("Employee record created for user: " + user.getUsername() + " with ID: " + user.getId());

        } catch (Exception e) {
            System.err.println("Failed to create employee record: " + e.getMessage());
            e.printStackTrace();
            // Rethrow to rollback the transaction
            throw new RuntimeException("Failed to create employee record", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            TokenResponse response = new TokenResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
        }
    }
}