package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.utils.TourGuideUtils;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestGpsService {

    @Autowired
    private GpsService service;

    private User user;

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    public void setup() {
        user = new User(uuid, "jon", "000", "jon@tourGuide.com");
        this.service.generateUserLocationHistory(user);
    }

    @Test
    public void trackUser() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);
        VisitedLocation visitedLocation = this.service.trackUserLocation(user);
        this.service.tracker.stopTracking();
        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);
        VisitedLocation visitedLocation = this.service.trackUserLocation(user);
        List<Attraction> attractions = this.service.getNearByAttractions(visitedLocation);
        this.service.tracker.stopTracking();
        assertEquals(5, attractions.size());
    }

    @Test
    public void getUserLocation() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);

        VisitedLocation visitedLocation = this.service.trackUserLocation(user);
        this.service.tracker.stopTracking();
        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }
}
