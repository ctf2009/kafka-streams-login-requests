package ctf.kafka.streams.loginviewer.controller;

import ctf.kafka.streams.loginviewer.service.NotificationRetrievalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/notifications")
public class NotificationsController {

    private NotificationRetrievalService notificationRetrievalService;

    public NotificationsController(final NotificationRetrievalService notificationRetrievalService) {
        this.notificationRetrievalService = notificationRetrievalService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getNotifications() {
        return ResponseEntity.ok(notificationRetrievalService.getNotifications());
    }


}
