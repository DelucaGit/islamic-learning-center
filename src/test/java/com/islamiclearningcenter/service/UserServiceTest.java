package com.islamiclearningcenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.islamiclearningcenter.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  @Test
  @DisplayName("emailExists delegates to repository")
  void emailExists_delegatesToRepository() {
    when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

    assertThat(userService.emailExists("a@b.com")).isTrue();
    verify(userRepository).existsByEmail("a@b.com");
  }
}
