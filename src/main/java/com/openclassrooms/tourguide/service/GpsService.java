package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.utils.TourGuideUtils;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
public class GpsService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1000);

    private final RewardsService rewardsService;

    private final GpsUtil gpsUtil;

    public final Tracker tracker = new Tracker();

    public GpsService(RewardsService rewardsService, GpsUtil gpsUtil) {
        this.rewardsService = rewardsService;
        this.gpsUtil = gpsUtil;
    }

    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }

    public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
                : trackUserLocation(user);
        this.executorService.shutdown();
        return visitedLocation;
    }

    public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
        if (executorService.isShutdown()) {
            return null;
        }

        CompletableFuture<VisitedLocation> future = CompletableFuture.supplyAsync(() -> {
            VisitedLocation visitedLocation = null;
            try {
                visitedLocation = this.getUserLocation(user);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            user.addToVisitedLocations(visitedLocation);
            rewardsService.calculateRewards(user);
            return visitedLocation;
        });
        return future.join();
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
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    /*
    Use for test purposes
     */

    public double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    public double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    public Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
