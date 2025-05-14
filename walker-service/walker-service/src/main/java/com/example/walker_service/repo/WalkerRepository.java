package com.example.walker_service.repo;

import com.example.walker_service.model.Walker;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// WalkerRepository.java
public interface WalkerRepository extends MongoRepository<Walker, String> {

    // Búsqueda por cercanía (radio de 5 km)
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    GeoJsonPoint location = null;

    boolean existsByUserId(String userId);

    Optional<com.example.walker_service.model.Walker> findByUserId(UUID userId);

    List<com.example.walker_service.model.Walker> findAllByUserId(String userId);



}