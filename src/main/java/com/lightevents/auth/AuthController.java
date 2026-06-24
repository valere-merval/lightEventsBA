package com.lightevents.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService service;

    public AuthController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthDtos.AccountResponse register(@Valid @RequestBody AuthDtos.RegisterRequest r) {
        return service.response(service.register(r));
    }

    @PostMapping("/verify")
    public AuthDtos.AccountResponse verify(@Valid @RequestBody AuthDtos.VerifyRequest r) {
        return service.response(service.verify(r));
    }

    @PostMapping("/login")
    public AuthDtos.LoginStartResponse login(@Valid @RequestBody AuthDtos.LoginRequest r) {
        return service.startEmailLogin(r);
    }

    @PostMapping("/login/request-code")
    public AuthDtos.LoginStartResponse requestLoginCode(@Valid @RequestBody AuthDtos.LoginRequest r) {
        return service.startEmailLogin(r);
    }

    @PostMapping("/login/verify")
    public AuthDtos.AccountResponse verifyLogin(@Valid @RequestBody AuthDtos.LoginVerifyRequest r) {
        return service.response(service.verifyEmailLogin(r));
    }
}
