package com.edstem.taxibookingandbillingsystem.model;

import com.edstem.taxibookingandbillingsystem.constant.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Taxi taxi;
    private String pickupLocation;
    private String dropoffLocation;
    private double fare;
    private LocalDateTime bookingTime;
    @Enumerated(EnumType.STRING)
    private Status status;
}
