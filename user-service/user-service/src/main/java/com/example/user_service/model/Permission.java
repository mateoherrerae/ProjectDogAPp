package com.example.user_service.model;

import jakarta.persistence.*;

import java.util.UUID;
// this class is used to define the permissions that a user can have
// the walker can accept_booking but no for delete_booking
// owner can delete_booking but no for accept_booking or can view_history but no modify_payments
// admin can do all the actions manage_users view_logs, etc
//@Entity
//public class Permission {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(unique = true, nullable = false)
//    private String name; // "CREATE_BOOKING", "EDIT_PROFILE", etc.
//}
