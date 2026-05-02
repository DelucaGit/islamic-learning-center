package com.islamiclearningcenter.service;

import com.islamiclearningcenter.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /** Returns whether an account with the given email already exists. */
  public boolean emailExists(String email) {
    return userRepository.existsByEmail(email);
  }
}
