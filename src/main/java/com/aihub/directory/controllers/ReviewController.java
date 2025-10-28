package com.aihub.directory.controllers;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Rating;
import com.aihub.directory.entities.User;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.RatingRepository;
import com.aihub.directory.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    private final AiToolRepository aiToolRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public ReviewController(AiToolRepository aiToolRepository, UserRepository userRepository, RatingRepository ratingRepository) {
        this.aiToolRepository = aiToolRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, Object> payload) {
        try {
            // 🔹 Extract data
            String aiName = (String) payload.get("aiName");
            String userEmail = (String) payload.get("userEmail");
            String comment = (String) payload.get("comment");
            BigDecimal ratingValue = new BigDecimal(payload.get("rating").toString());

            if (aiName == null || userEmail == null || ratingValue == null || comment == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            // 🔹 Find AI tool by name
            Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
            if (aiToolOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "AI tool not found: " + aiName));
            }

            AiTool aiTool = aiToolOpt.get();

            // 🔹 Find user by email
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found: " + userEmail));
            }

            User user = userOpt.get();

            // 🔹 Check if user already has a review
            Optional<Rating> existingReviewOpt = ratingRepository.findByAiToolAndUser(aiTool, user);

            Rating rating;
            String message;

            if (existingReviewOpt.isPresent()) {
                // ✅ Update existing review
                rating = existingReviewOpt.get();
                rating.setRatingValue(ratingValue);
                rating.setComment(comment);
                rating.setCreatedAt(LocalDateTime.now());
                message = "✅ Review updated successfully";
            } else {
                // ✅ Create new review
                rating = new Rating();
                rating.setAiTool(aiTool);
                rating.setUser(user);
                rating.setRatingValue(ratingValue);
                rating.setComment(comment);
                rating.setCreatedAt(LocalDateTime.now());
                message = "✅ Review submitted successfully";
            }

            Rating saved = ratingRepository.save(rating);

            // 🔹 Response to frontend
            Map<String, Object> response = Map.of(
                    "message", message,
                    "review", Map.of(
                            "user", user.getUsername(),
                            "rating", saved.getRatingValue(),
                            "comment", saved.getComment(),
                            "updatedAt", saved.getCreatedAt()
                    )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getReviewsByAiName(@RequestParam String aiName) {
        try {
            Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
            if (aiToolOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "AI tool not found: " + aiName));
            }

            AiTool aiTool = aiToolOpt.get();
            List<Rating> ratings = ratingRepository.findByAiTool(aiTool);

            if (ratings.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No reviews found for " + aiName, "reviews", List.of()));
            }

            // Map ratings into response DTOs
            List<Map<String, ? extends Serializable>> reviewList = ratings.stream()
                    .map(r -> Map.of(
                            "user", r.getUser().getUsername(),
                            "email", r.getUser().getEmail(),
                            "rating", r.getRatingValue(),
                            "comment", r.getComment(),
                            "createdAt", r.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("aiTool", aiTool.getName());
            response.put("reviewCount", reviewList.size());
            response.put("reviews", reviewList);

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
