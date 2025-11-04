package com.aihub.directory.services;

import com.aihub.directory.entities.User;
import com.aihub.directory.entities.UserActivityLog;
import com.aihub.directory.repositories.UserActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserActivityLogService {

    private final UserActivityLogRepository repository;

    public UserActivityLogService(UserActivityLogRepository repository) {
        this.repository = repository;
    }

    public void log(User user,
                    String eventType,
                    String entityType,
                    String entityId,
                    String entityName,
                    Map<String, Object> metadataJson) {
        UserActivityLog activity = new UserActivityLog(user, eventType, entityType, entityId, entityName, metadataJson);
        repository.save(activity);
    }
}
