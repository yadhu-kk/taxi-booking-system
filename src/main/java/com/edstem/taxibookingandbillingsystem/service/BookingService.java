package com.edstem.taxibookingandbillingsystem.service;

import com.edstem.taxibookingandbillingsystem.constant.Status;
import com.edstem.taxibookingandbillingsystem.contract.request.BookingRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.BookingResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.TaxiResponse;
import com.edstem.taxibookingandbillingsystem.exception.InsufficientFundException;
import com.edstem.taxibookingandbillingsystem.model.Booking;
import com.edstem.taxibookingandbillingsystem.model.Taxi;
import com.edstem.taxibookingandbillingsystem.model.User;
import com.edstem.taxibookingandbillingsystem.repository.BookingRepository;
import com.edstem.taxibookingandbillingsystem.repository.TaxiRepository;
import com.edstem.taxibookingandbillingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TaxiRepository taxiRepository;
    private final ModelMapper modelMapper;

    public BookingResponse bookTaxi(BookingRequest request, Long userId, Long taxiId, Long distance) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found" + userId));
        Taxi taxi = taxiRepository.findById(taxiId).orElseThrow(() -> new EntityNotFoundException("Taxi not found" + taxiId));
        Double expense = distance * 15d;
        if (expense > user.getAccountBalance()) {
            throw new InsufficientFundException();
        }
        Booking saveBooking = Booking.builder()
                .user(user)
                .taxi(taxi)
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .bookingTime(LocalDateTime.parse(LocalDateTime.now().toString()))
                .status(Status.CONFIRMED)
                .fare(expense)
                .build();
        user = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .accountBalance(user.getAccountBalance() - saveBooking.getFare())
                .build();
        user = userRepository.save(user);
        saveBooking = bookingRepository.save(saveBooking);
        return modelMapper.map(saveBooking, BookingResponse.class);
    }

    public BookingResponse viewBookingDetails(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("BookingNotFound" + bookingId));
        return modelMapper.map(booking, BookingResponse.class);
    }

    public String cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking Not found" + id));
        Booking updateBooking = Booking.builder()
                .id(id)
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .fare(booking.getFare())
                .bookingTime(booking.getBookingTime())
                .status(Status.CANCELLED)
                .build();
        bookingRepository.save(updateBooking);
        return "booking cancelled successfully" + id;
    }

    public List<TaxiResponse> searchNearestTaxi(String pickupLocation) {
        List<Taxi> taxiList = taxiRepository.findAll();
        List<Taxi> taxiAvailability = new ArrayList<>();
        for (Taxi taxies : taxiList) {
            if (taxies.getCurrentLocation().equals(pickupLocation)) {
                taxiAvailability.add(taxies);
            }
        }
        if (taxiAvailability.isEmpty()) {
            throw new EntityNotFoundException("taxi not available");
        } else {
            return taxiAvailability.stream()
                    .map(items -> modelMapper.map(items, TaxiResponse.class))
                    .collect(Collectors.toList());
        }
    }
}
