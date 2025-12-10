package com.openclassrooms.tourguide.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.openclassrooms.tourguide.service.*;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private final GpsService gpsService;

    private final RewardsService rewardsService;

    private final UserService userService;

    private final TripDealService tripDealService;

    public TourGuideController(GpsService gpsService, RewardsService rewardsService, UserService userService, TripDealService tripDealService) {
        this.gpsService = gpsService;
        this.rewardsService = rewardsService;
        this.userService = userService;
        this.tripDealService = tripDealService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
    	return this.gpsService.getUserLocation(getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public List<Attraction> getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
    	VisitedLocation visitedLocation = this.gpsService.getUserLocation(getUser(userName));
    	return this.gpsService.getNearByAttractions(visitedLocation);
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return this.userService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return this.tripDealService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return this.userService.getUser(userName);
    }
   

}