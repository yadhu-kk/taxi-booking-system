package com.edstem.taxibookingandbillingsystem.contract.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaxiRequest {
    @NotBlank(message = "please enter drivername")
    private String driverName;
    @NotBlank(message = "please enter LicenceNumber")
    private String licenceNumber;
    @NotBlank(message = "please enter currentLocation")
    private String currentLocation;

}
