package kr.io.blankspace.dto.account.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNameRequestDTO(@NotBlank @Size(max = 10) String userName) {}