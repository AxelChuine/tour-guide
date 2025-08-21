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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@ExtendWith(SpringExtension.class)
public class TestRewardsService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @InjectMocks
    private RewardsService rewardsService;

    @Mock
    private final GpsService gpsService;

    @Mock
    private final UserService userService;

    @Mock
    private GpsUtil gpsUtil;

    public final Tracker tracker;

    public TestRewardsService(GpsService gpsService, UserService userService, Tracker tracker) {
        this.gpsService = gpsService;
        this.userService = userService;
        this.tracker = tracker;
    }

    @Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, tracker);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		/*tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();*/

        try {
            this.gpsService.trackUserLocation(user);
            List<UserReward> userRewards = user.getUserRewards();
            tourGuideService.tracker.stopTracking();
            assertTrue(userRewards.size() == 1);
        } finally {
            executorService.shutdown();
        }

	}

	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Disabled // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, tracker);

		rewardsService.calculateRewards(this.userService.getAllUsers().get(0));
		List<UserReward> userRewards = this.userService.getUserRewards(this.userService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
