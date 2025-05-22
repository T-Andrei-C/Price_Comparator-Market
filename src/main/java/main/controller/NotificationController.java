package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.user.Notification;
import main.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/email/{email}")
    private List<Notification> getUserNotifications (@PathVariable String email) {
        return notificationService.getUserNotifications(email);
    }

    @PatchMapping("/notification/{notificationId}/user/email/{email}")
    private ResponseEntity<String> notificationSeen (@PathVariable Long notificationId, @PathVariable String email) {
        return ResponseEntity.ok(notificationService.notificationSeen(notificationId, email));
    }

}
