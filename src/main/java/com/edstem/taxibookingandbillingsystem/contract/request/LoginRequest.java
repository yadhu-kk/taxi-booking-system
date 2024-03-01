package com.edstem.taxibookingandbillingsystem.contract.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @Email private String email;

    @NotBlank(message = "Field should not be empty")
    private String password;
}
