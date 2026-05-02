package model;

public class RateLimitConfig {
    private final int maxRequests;
    private final int timeWindowInSeconds;

    //constructors
    public RateLimitConfig(int maxRequests, int timeWindowInSeconds) {
        this.maxRequests = maxRequests; // max Requests allowed in the time window
        this.timeWindowInSeconds = timeWindowInSeconds; // duration of the time window in seconds
    }

    //getters
    public int getMaxRequests() {
        return maxRequests;
    }
    public int getTimeWindowInSeconds() {
        return timeWindowInSeconds;
    }
}
