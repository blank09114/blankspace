package kr.io.blankspace.account.dto.user;

import jakarta.validation.constraints.Size;

public record BlockToggleRequest(@Size(max = 20) String reason) {}