package com.example.booking_service.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class NotificationRequest {
    private List<String> walkerIds;
    private String message;
    private String bookingId;
    private LocalDateTime expiresAt;


}
