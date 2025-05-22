package main.service;

import lombok.RequiredArgsConstructor;
import main.model.user.Notification;
import main.model.user.User;
import main.repository.NotificationRepository;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public List<Notification> getUserNotifications (String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            return user.getNotifications();
        }

        return new ArrayList<>();
    }

    public String notificationSeen (Long notificationId, String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null) {
                notification.setSeen(notification.getSeen());
                notificationRepository.save(notification);

                return "Notification saved successfully";
            } else {
                return "Notification does not exist";
            }
        }

        return "User does not exist";
    }

}
