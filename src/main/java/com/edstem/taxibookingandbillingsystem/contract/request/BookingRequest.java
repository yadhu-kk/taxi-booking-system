package com.edstem.taxibookingandbillingsystem.contract.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookingRequest {
    @NotBlank(message = "Pickup location should not be blank")
    private String pickupLocation;

    @NotBlank(message = "Drop-off location should not be blank")
    private String dropoffLocation;
}
