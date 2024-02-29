package com.edstem.taxibookingandbillingsystem.contract.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdateAccountRequest {
    @NotBlank(message = "It cannot be empty")
    private double accountBalance;
}
