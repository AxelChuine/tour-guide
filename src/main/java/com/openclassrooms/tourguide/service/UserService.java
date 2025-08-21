package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
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

    private final Map<String, User> internalUserMap = new HashMap<>();

    public UserService(GpsService gpsService, RewardsService rewardsService) {
        this.gpsService = gpsService;
        this.rewardsService = rewardsService;
    }


    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }



}
