package com.aihub.directory.config;

import com.aihub.directory.entities.User;
import com.aihub.directory.repositories.UserRepository;
import com.aihub.directory.services.UserActivityLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ActivityLoggingFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final UserActivityLogService activityService;

    // 🧠 Cache to store recent logs (key: userEmail + entityType + entityId)
    private static final Map<String, Instant> recentLogs = new ConcurrentHashMap<>();

    // 🕒 Define how long to skip duplicate logs (e.g. 2 minutes)
    private static final long COOLDOWN_SECONDS = 120;

    // Regex patterns to extract entity IDs or names
    private static final Pattern CATEGORY_ID_PATTERN = Pattern.compile("/api/categories/(\\d+)");
    private static final Pattern AI_TOOL_NAME_PATTERN = Pattern.compile("/api/ai-tools/name/([^/]+)");

    public ActivityLoggingFilter(UserRepository userRepository, UserActivityLogService activityService) {
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Jwt jwt) {
                String email = jwt.getClaimAsString("email");

                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        String path = request.getRequestURI();
                        String method = request.getMethod();

                        String entityType = null;
                        String entityId = null;

                        // Determine entity type and extract identifier
                        if (path.contains("/ai-tools")) {
                            entityType = "AI_TOOL";
                            Matcher matcher = AI_TOOL_NAME_PATTERN.matcher(path);
                            if (matcher.find()) {
                                entityId = matcher.group(1).toLowerCase();
                            }
                        } else if (path.contains("/categories")) {
                            entityType = "CATEGORY";
                            Matcher matcher = CATEGORY_ID_PATTERN.matcher(path);
                            if (matcher.find()) {
                                entityId = matcher.group(1);
                            }
                        }

                        // ✅ Log only relevant endpoints
                        if (entityType != null && entityId != null) {

                            // Build a smart unique key for cooldown
                            String key = email + ":" + entityType + ":" + entityId;
                            Instant now = Instant.now();

                            // Skip duplicate "VIEW" logs within cooldown window
                            if (method.equals("GET")) {
                                Instant lastLogged = recentLogs.get(key);
                                if (lastLogged != null && now.minusSeconds(COOLDOWN_SECONDS).isBefore(lastLogged)) {
                                    filterChain.doFilter(request, response);
                                    return;
                                }
                                recentLogs.put(key, now);
                            }

                            // Determine event type
                            String eventType = switch (method) {
                                case "GET" -> "VIEW";
                                case "POST" -> "CREATE";
                                case "PUT" -> "UPDATE";
                                case "DELETE" -> "DELETE";
                                default -> "ACTION";
                            };

                            Map<String, Object> metadata = Map.of(
                                    "path", path,
                                    "method", method,
                                    "ip", request.getRemoteAddr()
                            );

                            // ✅ Log to DB
                            activityService.log(
                                    userOpt.get(),
                                    eventType,
                                    entityType,
                                    entityId,
                                    path,
                                    metadata
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Activity logging failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
