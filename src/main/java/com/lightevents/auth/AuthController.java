package com.lightevents.auth;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AccountService service;
    public AuthController(AccountService service){this.service=service;}
    @PostMapping("/register") public AuthDtos.AccountResponse register(@Valid @RequestBody AuthDtos.RegisterRequest r){return service.response(service.register(r));}
    @PostMapping("/verify") public AuthDtos.AccountResponse verify(@Valid @RequestBody AuthDtos.VerifyRequest r){return service.response(service.verify(r));}
    @PostMapping("/login") public AuthDtos.AccountResponse login(@Valid @RequestBody AuthDtos.LoginRequest r){return service.response(service.login(r));}
}
