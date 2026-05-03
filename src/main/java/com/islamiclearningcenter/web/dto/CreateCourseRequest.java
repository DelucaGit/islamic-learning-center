package com.islamiclearningcenter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
    @NotBlank @Size(max = 500) String title,
    @Size(max = 20000) String description,
    Boolean active) {}
