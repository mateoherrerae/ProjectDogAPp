package com.example.walker_service.service;

import com.example.walker_service.model.Review;
import com.example.walker_service.model.Walker;
import com.example.walker_service.repo.WalkerRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalkerService {

    private final WalkerRepository walkerRepository;
    private final JwtService jwtService;
    private final MongoTemplate mongoTemplate;

    public WalkerService(WalkerRepository walkerRepository, JwtService jwtService, MongoTemplate mongoTemplate) {
        this.walkerRepository = walkerRepository;
        this.jwtService = jwtService;
        this.mongoTemplate = mongoTemplate;
    }

    public Walker registerWalker(Walker walker, String token) {
        UUID userId = jwtService.extractUserId(token);
        walker.setUserId(userId.toString());
        walker.setId(UUID.randomUUID().toString());
        return walkerRepository.save(walker);
    }
    public Walker getWalkerById(UUID id) {
        return walkerRepository.findById(String.valueOf(id)).orElse(null);
    }

    public List<Walker> findNearbyWalkers(double lat, double lng) {
        Distance distance = new Distance(6, Metrics.KILOMETERS); // Adjust the distance as needed

        Query query = new Query();
        query.addCriteria(Criteria.where("location").nearSphere(new Point(lat, lng)).maxDistance(distance.getNormalizedValue()));

        return mongoTemplate.find(query, Walker.class);
    }

    public double calculateAverageRating(Walker walker) {
        if (walker.getReviews().isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (Review review : walker.getReviews()) {
            if (review != null) {
                sum += review.getRating();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }


    public boolean existsByUserId(UUID userId) {
        return walkerRepository.existsByUserId(userId.toString());
    }

    public Walker getWalkerByUserId(String userId) {
        List<Walker> walkers = walkerRepository.findAllByUserId(userId);
        if (walkers.size() != 1) {
            throw new IllegalStateException("Expected one walker, but found " + walkers.size());
        }
        return walkers.get(0);
    }

    public Walker saveWalker(Walker walker) {
        return walkerRepository.save(walker);
    }
}
