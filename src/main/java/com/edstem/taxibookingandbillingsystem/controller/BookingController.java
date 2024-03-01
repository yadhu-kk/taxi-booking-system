package com.edstem.taxibookingandbillingsystem.controller;

import com.edstem.taxibookingandbillingsystem.contract.request.BookingRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.BookingResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.TaxiResponse;
import com.edstem.taxibookingandbillingsystem.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/{userId}/{taxiId}")
    public BookingResponse bookTaxi(
            @Valid @RequestBody BookingRequest request,
            @PathVariable Long userId,
            @PathVariable Long taxiId,
            @RequestParam Long distance) {
        return bookingService.bookTaxi(request, userId, taxiId, distance);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse viewBooking(@PathVariable Long bookingId) {
        return bookingService.viewBookingDetails(bookingId);
    }

    @PutMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @GetMapping("/searchTaxi")
    public List<TaxiResponse> searchNearestTaxi(@RequestParam String pickupLocation) {
        return bookingService.searchNearestTaxi(pickupLocation);
    }
}
