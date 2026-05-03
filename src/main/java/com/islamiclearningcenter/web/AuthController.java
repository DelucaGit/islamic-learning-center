package com.islamiclearningcenter.web;

import com.islamiclearningcenter.auth.AccessAndRefreshTokens;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.service.LoginService;
import com.islamiclearningcenter.service.RegistrationService;
import com.islamiclearningcenter.web.dto.LoginRequest;
import com.islamiclearningcenter.web.dto.RefreshTokenRequest;
import com.islamiclearningcenter.web.dto.RegisterRequest;
import com.islamiclearningcenter.web.dto.RegisteredUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final RegistrationService registrationService;
  private final LoginService loginService;

  public AuthController(RegistrationService registrationService, LoginService loginService) {
    this.registrationService = registrationService;
    this.loginService = loginService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegisteredUserResponse register(@Valid @RequestBody RegisterRequest request) {
    User user =
        registrationService.registerNewUser(
            request.email(), request.password(), request.fullName(), request.role());
    return RegisteredUserResponse.fromEntity(user);
  }

  @PostMapping("/login")
  public AccessAndRefreshTokens login(@Valid @RequestBody LoginRequest request) {
    return loginService.login(request.email(), request.password());
  }

  @PostMapping("/refresh")
  public AccessAndRefreshTokens refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return loginService.refresh(request.refreshToken());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody RefreshTokenRequest request) {
    loginService.logout(request.refreshToken());
  }
}
