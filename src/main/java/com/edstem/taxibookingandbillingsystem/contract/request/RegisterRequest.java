package com.edstem.taxibookingandbillingsystem.contract.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterRequest {
    @NotBlank(message = "Name should not be empty")
    private String name;

    @NotBlank(message = "Email should not be empty")
    @Email
    private String email;

    @NotBlank(message = "Password should not be empty")
    private String password;
}
