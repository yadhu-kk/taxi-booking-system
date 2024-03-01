package com.edstem.taxibookingandbillingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

public class BookingServiceTest {
    @Mock private BookingRepository bookingRepository;
    @Mock private ModelMapper modelMapper;
    @InjectMocks private BookingService bookingService;
    @Mock private TaxiRepository taxiRepository;
    @Mock private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testBookTaxi() {
//        User user = new User();
//        user.setAccountBalance(10.0d);
//        user.setEmail("yadhu@gmail.com");
//        user.setId(1L);
//        user.setName("yadhu");
//        user.setPassword("password");
//        Optional<User> ofResult = Optional.of(user);
//        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
//        when(taxiRepository.findById(Mockito.<Long>any()))
//                .thenThrow(new InsufficientFundException());
//        assertThrows(
//                InsufficientFundException.class,
//                () -> bookingService.bookTaxi(new BookingRequest("kochi", "kannur"), 1L, 1L, 1L));
//        verify(taxiRepository).findById(Mockito.<Long>any());
//        verify(userRepository).findById(Mockito.<Long>any());
//    }

    @Test
    void testBookTaxi() {
        User user = new User(1L, "prashob", "prashob@gmail.com", "password", 1000.0);
        Taxi taxi =
                Taxi.builder()
                        .driverName("yadhu")
                        .licenceNumber("5555")
                        .currentLocation("Kakkanad")
                        .build();

        BookingRequest request = new BookingRequest("Kakkanad", "Aluva");
        Long userId=1L;
        Long taxiId = 1L;
        Long distance = 8L;
        Double expense = distance * 10D;
        Booking expectedBooking =
                Booking.builder()
                        .user(user)
                        .taxi(taxi)
                        .pickupLocation(request.getPickupLocation())
                        .dropoffLocation(request.getDropoffLocation())
                        .bookingTime(LocalDateTime.parse(LocalDateTime.now().toString()))
                        .fare(expense)
                        .status(Status.CONFIRMED)
                        .build();
        User updatedUser =
                User.builder()
                        .id(userId)
                        .id(taxiId)
                        .name(user.getName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .accountBalance(user.getAccountBalance() - expectedBooking.getFare())
                        .build();

        BookingResponse expectedResponse = modelMapper.map(expectedBooking, BookingResponse.class);
        when(taxiRepository.findById(taxiId)).thenReturn(Optional.of(taxi));
        when(bookingRepository.save(any())).thenReturn(expectedBooking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);

        BookingResponse actualResponse = bookingService.bookTaxi(request, userId,taxiId,distance);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testBookTaxi_InsufficientFund() {
        User user = new User(1L, "yadhu", "yadhu@gmail.com", "password", 1000.0);
        Taxi taxi =
                Taxi.builder()
                        .driverName("yadhu")
                        .licenceNumber("5555")
                        .currentLocation("kakkanad")
                        .build();

        BookingRequest request = new BookingRequest("Kochi", "kannur");
        Long taxiId = 1L;
        Long userId = 2L;
        Long distance = 80L;
        when(taxiRepository.findById(taxiId)).thenReturn(Optional.of(taxi));

        assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.bookTaxi(request, userId, taxiId, distance);
                });
    }

    @Test
    void testGetBookingDetails() {
        Long bookingId = 1L;
        Taxi taxi = new Taxi(1L, "akhil", "25A4", "kochi");
        User user = new User(1L, "yadhu", "yadhu@email.com", "password", 100.0);
        Booking booking =
                new Booking(
                        1L,
                        user,
                        taxi,
                        "kochi",
                        "kannur",
                        120.0,
                        LocalDateTime.now(),
                        Status.CONFIRMED);
        BookingResponse expectedResponse =
                new BookingResponse(
                        1L,
                        2L,
                        3L,
                        "kochi",
                        "kannur",
                        2d,
                        "2024-02-03 10:18:28.012172",
                        Status.CONFIRMED);

        when(bookingRepository.findById(Mockito.eq(bookingId))).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingResponse.class)).thenReturn(expectedResponse);

        BookingResponse actualResponse = bookingService.viewBookingDetails(bookingId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testCancelBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Long taxiId = 1L;
        User user = new User(userId, "yadhu", "yadhu@gmail.com", "password", 100.0);
        Taxi taxi = new Taxi(taxiId, "name", "2554", "location");
        Booking booking =
                Booking.builder()
                        .user(user)
                        .taxi(taxi)
                        .pickupLocation("location1")
                        .dropoffLocation("location2")
                        .fare(15.00)
                        .bookingTime(LocalDateTime.now())
                        .status(Status.CONFIRMED)
                        .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        String actualResponse = bookingService.cancelBooking(bookingId);

        assertEquals("booking cancelled successfully" + bookingId, actualResponse);
    }

    @Test
    void testSearchNearestTaxi() {
        Taxi taxiOne = new Taxi(1L, "yadhu", "4080", "Kakkanad");
        Taxi taxiTwo = new Taxi(1L, "prashob", "8588", "Kakkanad");

        List<Taxi> availableTaxies = Arrays.asList(taxiOne, taxiTwo);
        when(taxiRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(
                EntityNotFoundException.class, () -> bookingService.searchNearestTaxi("Kakkanad"));
        when(taxiRepository.findAll()).thenReturn(availableTaxies);

        List<TaxiResponse> expectedResponse =
                availableTaxies.stream()
                        .map(taxi -> modelMapper.map(taxi, TaxiResponse.class))
                        .collect(Collectors.toList());

        List<TaxiResponse> actualResponse = bookingService.searchNearestTaxi("Kakkanad");
        assertEquals(expectedResponse, actualResponse);
    }
}
