package com.example.walker_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalkerDetailsResponse {
    private String id;          // ID del perfil en Walker Service
    private double hourlyRate;  // Tarifa por hora
    private List<String> serviceAreas; // Zonas donde trabaja (ej: ["Palermo", "Recoleta"])
    private GeoJsonPoint location;      // Coordenadas geográficas (para mostrar en mapa)
    private Map<String, List<String>> availability; // Horarios disponibles (ej: {"Lunes": ["09:00", "17:00"]})
    private List<String> certifications; // Certificaciones (ej: ["Primeros auxilios caninos"])
    private double rating;      // Promedio de reseñas (si aplica)
    private String description;  // Descripción personal

}
