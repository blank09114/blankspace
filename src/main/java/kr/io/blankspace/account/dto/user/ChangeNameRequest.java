package kr.io.blankspace.account.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNameRequest(@NotBlank @Size(max = 10) String userName) {}