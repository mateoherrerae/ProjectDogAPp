package com.example.dog_service.repo;

import com.example.dog_service.model.Dog;
import com.example.dog_service.model.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DogRepository extends JpaRepository<Dog, UUID> {
    List<Dog> findByOwnerId(UUID ownerId);
    List<Dog> findByBreedAndIsPublicProfileTrue(DogBreed breed);
}
