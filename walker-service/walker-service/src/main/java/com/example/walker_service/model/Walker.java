package com.example.walker_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Document(collection = "walkers")
@Getter
@Setter
public class Walker {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId; // ID del usuario en el User Service
    private String description;
    private double hourlyRate;
    private List<String> certifications; // ["Primeros auxilios", "Entrenador canino"]
    private List<String> serviceAreas; // ["Barrio A", "Barrio B"]
    private GeoJsonPoint location; // Coordenadas {type: "Point", coordinates: [lat, lng]}
    private Map<String, List<LocalTime>> availability; // Ej: {"Lunes": ["09:00", "17:00"]}

    @DBRef
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();  // Reseñas embebidas

}