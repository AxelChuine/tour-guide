package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class GpsService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1000);

    private final RewardsService rewardsService;

    private final GpsUtil gpsUtil;

    public GpsService(RewardsService rewardsService, GpsUtil gpsUtil) {
        this.rewardsService = rewardsService;
        this.gpsUtil = gpsUtil;
    }


    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
                : trackUserLocation(user);
        this.executorService.shutdown();
        return visitedLocation;
    }

    public VisitedLocation trackUserLocation(User user) {
        if (executorService.isShutdown()) {
            return null;
        }
        VisitedLocation visitedLocation = this.getUserLocation(user);
        Future<VisitedLocation> f;
        user.addToVisitedLocations(visitedLocation);
        this.executorService.submit(() -> {
            rewardsService.calculateRewards(user);
        });
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        Set<Attraction> nearbyAttractions = new HashSet<>();
        Map<Double, Attraction> distance = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (Attraction attraction : this.gpsUtil.getAttractions()) {
            Double d = this.rewardsService.getDistance(attraction, visitedLocation.location);
            distances.add(d);
            distance.put(d, attraction);
        }
        Double[] array = new Double[distances.size()];
        array =  distances.toArray(array);
        Arrays.sort(array);
        Double[] distancesToRemember = Arrays.copyOf(array, 5);
        for (Double  d : distancesToRemember) {
            nearbyAttractions.add(distance.get(d));
        }
        return nearbyAttractions.stream().toList();
    }

    public void generateUserLocationHistory(User user) {

    }
}
