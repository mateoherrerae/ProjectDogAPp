package com.example.user_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GeoPoint {

    private double lat;
    private double lng;

    public GeoPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
