package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import com.openclassrooms.tourguide.utils.TourGuideUtils;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class UserService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1000);

    private final GpsService gpsService;

    private final RewardsService rewardsService;

    private final TourGuideUtils utils;

    public Tracker tracker = new Tracker();

    public UserService(GpsService gpsService, RewardsService rewardsService, TourGuideUtils utils) {
        this.gpsService = gpsService;
        this.rewardsService = rewardsService;
        this.utils = utils;
    }


    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public User getUser(String userName) {
        return this.utils.internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(this.utils.internalUserMap.values());
    }

    public void addUser(User user) {
        if (!this.utils.internalUserMap.containsKey(user.getUserName())) {
            this.utils.internalUserMap.put(user.getUserName(), user);
        }
    }



}
