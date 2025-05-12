package com.example.dog_service.service;

import com.example.dog_service.exception.DogNotFoundException;
import com.example.dog_service.feign.UserServiceClient;
import com.example.dog_service.model.Dog;
import com.example.dog_service.model.DogRequest;
import com.example.dog_service.model.DogResponse;
import com.example.dog_service.repo.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserServiceClient userServiceClient; // Feign Client para User Service

    public DogResponse createDog(DogRequest request, UUID ownerId) {
        Dog dog = new Dog();
        dog.setName(request.getName());
        dog.setBreed(request.getBreed());
        dog.setAge(request.getAge());
        dog.setWeight(request.getWeight());
        dog.setMedicalHistory(request.getMedicalHistory());
        dog.setSpecialCareInstructions(request.getSpecialCareInstructions());
        dog.setPublicProfile(request.isPublicProfile());
        dog.setOwnerId(ownerId);

        Dog savedDog = dogRepository.save(dog);
        return convertToDogResponse(savedDog);
    }

    // Método auxiliar para convertir Dog a DogResponse
    private DogResponse convertToDogResponse(Dog dog) {
        String ownerUsername = userServiceClient.getUsernameById(dog.getOwnerId()).getBody();

        return DogResponse.builder()
                .id(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .weight(dog.getWeight())
                .medicalHistory(dog.getMedicalHistory())
                .specialCareInstructions(dog.getSpecialCareInstructions()) // Ensure this method exists
                .build();
    }

    public DogResponse getPublicDogProfile(UUID dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new DogNotFoundException(dogId));

        return mapToResponse(dog, false);
    }

    public List<DogResponse> getDogsByOwner(UUID ownerId) {
        List<Dog> dogs = dogRepository.findByOwnerId(ownerId);
        return dogs.stream()
                .map(dog -> mapToResponse(dog, true))
                .collect(Collectors.toList());
    }

    public DogResponse getDogProfile(UUID dogId, boolean isOwner) {
        Dog dog = findDogById(dogId);
        return DogResponse.builder()
                .id(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .weight(isOwner ? dog.getWeight() : null)
                .medicalHistory(isOwner ? dog.getMedicalHistory() : null)
                .specialCareInstructions(isOwner ? dog.getSpecialCareInstructions() : null) // Asegúrate de mapear este campo
                .isPublicProfile(dog.isPublicProfile())
                .build();
    }

    public boolean isDogOwner(UUID dogId, String username) {
        // Obtener el userId a partir del username usando el FeignClient
        UUID ownerId = userServiceClient.getUserIdByUsername(username).getBody();
        return checkDogOwnership(dogId, ownerId);
    }

    private boolean checkDogOwnership(UUID dogId, UUID ownerId) {
        Dog dog = findDogById(dogId);
        return dog.getOwnerId().equals(ownerId);
    }

    private DogResponse mapToResponse(Dog dog, boolean isOwner) {
        String ownerUsername = userServiceClient.getUsernameById(dog.getOwnerId()).getBody();

        return DogResponse.builder()
                .id(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .weight(isOwner ? dog.getWeight() : null)
                .medicalHistory(isOwner ? dog.getMedicalHistory() : null)
                .specialCareInstructions(isOwner ? dog.getSpecialCareInstructions() : null)
                .isPublicProfile(dog.isPublicProfile()) // Use the correct field name
                .build();
    }

    public Dog findDogById(UUID dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new DogNotFoundException(dogId));
    }
}