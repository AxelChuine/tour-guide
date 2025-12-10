package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.openclassrooms.tourguide.tracker.Tracker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@SpringBootTest
public class TestRewardsService {

    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private GpsService gpsService;

    @Autowired
    private UserService userService;

    @Autowired
    private GpsUtil gpsUtil;


    @Test
	public void userGetRewards() throws ExecutionException, InterruptedException {
		InternalTestHelper.setInternalUserNumber(0);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsService.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        this.gpsService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        this.userService.tracker.stopTracking();
        assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = gpsService.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	//@Disabled // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() throws InterruptedException {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);

        List<Attraction> attractions = gpsService.getAttractions();
        User user = this.userService.getAllUsers().get(0);
        // Ensure there is at least one visited location so calculateRewards has input to process
        for (Attraction attraction : attractions) {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        }

		rewardsService.calculateRewards(user);

        // Wait up to 5 seconds for asynchronous reward calculation to complete
        int expected = attractions.size();
        long start = System.currentTimeMillis();
        while (user.getUserRewards().size() < expected && System.currentTimeMillis() - start < 5000) {
            Thread.sleep(50);
        }

		List<UserReward> userRewards = this.userService.getUserRewards(user);
		rewardsService.tracker.stopTracking();
		userService.tracker.stopTracking();

		assertEquals(attractions.size(), userRewards.size());
	}

}
