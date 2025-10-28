package com.aihub.directory.controllers;

import com.aihub.directory.entities.User;
import com.aihub.directory.repositories.UserRepository;
import com.aihub.directory.security.GoogleTokenVerifierService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private GoogleTokenVerifierService googleVerifier;

    @Autowired
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/google")
    @Transactional
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        var payloadData = googleVerifier.verifyToken(token);

        if (payloadData == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google token"));
        }

        String email = payloadData.getEmail();
        String name = (String) payloadData.get("name");
        String picture = (String) payloadData.get("picture");

        // find existing user by email
        Optional<User> existing = userRepository.findByEmail(email);

        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            // Create new user
            user = new User();
            // Build username from name or email local-part
            String baseUsername = (name != null && !name.isBlank())
                    ? slugifyName(name)
                    : email.split("@")[0];

            String uniqueUsername = makeUsernameUnique(baseUsername);
            user.setUsername(uniqueUsername);
            user.setEmail(email);
            user.setCreatedAt(LocalDateTime.now());

            user = userRepository.save(user);
        }

        Map<String, Object> response = Map.of(
                "name", name,
                "email", email,
                "picture", picture,
                "userId", user.getId(),
                "message", "✅ Verified successfully"
        );

        return ResponseEntity.ok(response);
    }

    // create a simple slug: lowercase, remove non-alphanumeric, replace spaces with dot
    private String slugifyName(String name) {
        return name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", ".");
    }

    // ensure username uniqueness by appending a number suffix if needed
    private String makeUsernameUnique(String base) {
        String candidate = base;
        int attempt = 0;
        while (userRepository.findByUsername(candidate).isPresent()) {
            attempt++;
            candidate = base + (attempt);
            // You could also add randomness if you prefer (e.g., base + "_" + randomShort)
        }
        return candidate;
    }
}

