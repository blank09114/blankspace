package kr.io.blankspace.dto.account.user;

import jakarta.validation.constraints.Size;

public record BlockToggleRequest(@Size(max = 20) String reason) {}