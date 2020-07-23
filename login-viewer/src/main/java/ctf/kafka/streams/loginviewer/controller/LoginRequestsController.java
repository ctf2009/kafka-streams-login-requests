package ctf.kafka.streams.loginviewer.controller;

import ctf.kafka.streams.loginviewer.service.LoginRequestsRetrievalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/requests")
public class LoginRequestsController {

    private LoginRequestsRetrievalService loginRequestsRetrievalService;

    public LoginRequestsController(final LoginRequestsRetrievalService loginRequestsRetrievalService) {
        this.loginRequestsRetrievalService = loginRequestsRetrievalService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getLoginRequests() {
        return ResponseEntity.ok(loginRequestsRetrievalService.getLoginRequests());
    }

}
