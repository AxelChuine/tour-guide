package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.user.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class TestGpsService {

    @InjectMocks
    private GpsService service;

    @Test
    public void trackUser() {
        InternalTestHelper.setInternalUserNumber(0);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = this.gpsService.trackUserLocation(user);
        this.service.tracker.stopTracking();
        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Disabled // Not yet implemented
    @Test
    public void getNearbyAttractions() {
        InternalTestHelper.setInternalUserNumber(0);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = this.gpsService.trackUserLocation(user);
        List<Attraction> attractions = this.gpsService.getNearByAttractions(visitedLocation);
        this.service.tracker.stopTracking();
        assertEquals(5, attractions.size());
    }

    @Test
    public void getUserLocation() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = this.gpsService.trackUserLocation(user);
        this.service.tracker.stopTracking();
        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }
}
