package com.familymemories.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.security.family-email}")
    private String familyEmail;

    @Value("${app.security.family-password}")
    private String familyPassword;

    // POST /api/auth/check   body: { "email": "...", "password": "..." }
    // Used by the frontend login screen. Returns 200 if correct, 401 if not.
    @PostMapping("/check")
    public ResponseEntity<?> checkLogin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        boolean emailOk = familyEmail.equalsIgnoreCase(email == null ? "" : email.trim());
        boolean passwordOk = familyPassword.equals(password);

        if (emailOk && passwordOk) {
            return ResponseEntity.ok().body(Map.of("ok", true));
        }
        return ResponseEntity.status(401).body(Map.of("ok", false, "message", "Wrong email or password"));
    }
}