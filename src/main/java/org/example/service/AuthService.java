package org.example.service;

public interface AuthService {

    String authenticateAndGenerateToken(String username, String password);
}
