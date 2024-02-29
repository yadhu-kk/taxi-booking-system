package com.edstem.taxibookingandbillingsystem.service;

import com.edstem.taxibookingandbillingsystem.contract.request.TaxiRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.TaxiResponse;
import com.edstem.taxibookingandbillingsystem.model.Taxi;
import com.edstem.taxibookingandbillingsystem.repository.TaxiRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaxiService {
    private final TaxiRepository taxiRepository;
    private final ModelMapper modelMapper;

    public TaxiResponse addTaxi(TaxiRequest request) {
//        Taxi taxi=Taxi.builder()
//                .driverName(request.getDriverName())
//                .licenceNumber(request.getLicenceNumber())
//                .currentLocation(request.getCurrentLocation())
//                .build();
//        taxi=taxiRepository.save(taxi);
//        return modelMapper.map(taxi,TaxiResponse.class);
        if (taxiRepository.existsByLicenceNumber(request.getLicenceNumber())) {
            throw new EntityExistsException("Licence number already exist");
        }
        Taxi taxi = modelMapper.map(request, Taxi.class);
        taxi = taxiRepository.save(taxi);
        return modelMapper.map(taxi, TaxiResponse.class);
    }
}
