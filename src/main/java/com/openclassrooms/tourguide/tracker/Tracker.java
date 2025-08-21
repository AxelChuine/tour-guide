package com.openclassrooms.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.openclassrooms.tourguide.utils.TourGuideUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import org.springframework.stereotype.Component;

@Component
public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private boolean stop = false;

	public Tracker() {
		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	/*@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		if (Thread.currentThread().isInterrupted() || stop) {
			logger.debug("Tracker stopping");
		}

		List<User> users = tourGuideService.getAllUsers();
		for (int i = 0; i < users.size(); i++) {
			logger.debug("Begin Tracker. Tracking {} users.", users.size());
			stopWatch.start();
			users.forEach(tourGuideService::trackUserLocation);
			stopWatch.stop();
			logger.debug("Tracker Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
			stopWatch.reset();
			logger.debug("Tracker sleeping");
			*//*TimeUnit.SECONDS.sleep(trackingPollingInterval);*//*
		}
	}*/

}
