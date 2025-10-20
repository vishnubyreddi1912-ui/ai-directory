package com.aihub.directory.controllers;

import com.aihub.directory.security.GoogleTokenVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:4200")
public class AuthController {

    @Autowired
    private GoogleTokenVerifierService googleVerifier;

    @PostMapping("/google")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        var payloadData = googleVerifier.verifyToken(token);

        if (payloadData == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google token"));
        }

        String email = payloadData.getEmail();
        String name = (String) payloadData.get("name");
        String picture = (String) payloadData.get("picture"); // ✅ Extract picture

        Map<String, Object> response = Map.of(
                "name", name,
                "email", email,
                "picture", picture,
                "message", "✅ Verified successfully"
        );

        return ResponseEntity.ok(response);
    }
}

