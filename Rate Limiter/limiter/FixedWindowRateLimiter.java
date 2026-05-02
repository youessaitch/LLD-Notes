package limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;

import enums.RateLimitType;
import model.RateLimitConfig;

public class FixedWindowRateLimiter extends RateLimiter {
   private final Map<String, Integer> requestCount = new ConcurrentHashMap<>(); //userId → number of requests in current window
    private final Map<String, Long> windowStart = new HashMap<>(); //userId → which window they are currently in

    public FixedWindowRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.FIXED_WINDOW); //call parent constructor to set config and type
    }

    @Override
    public boolean allowRequest(String userId) {
    AtomicBoolean allowed = new AtomicBoolean(false);  // Thread-safe flag to track if request is allowed
    
    long currReqWindow = System.currentTimeMillis() / 1000 / config.getTimeWindowInSeconds();  // Calculate current windowID
    
    requestCount.compute(userId, (id, count) -> {  // Atomically update the request count for this user
        long lastReqWindow = windowStart.getOrDefault(id, currReqWindow);  // Get the last window this user was in, else default to current window
        
        if (lastReqWindow != currReqWindow) {  // If user is in a new window
            windowStart.put(id, currReqWindow);  // Update the window start for this user
            allowed.set(true);  // Allow the request (first in new window)
            return 1;  // Reset count to 1
        }
        
        if (count == null) count = 0;  // Handle first-time user (no previous count)
        
        if (count < config.getMaxRequests()) {  // If under the limit
            allowed.set(true);  // Allow the request
            return count + 1;  // Increment count
        }
        
        return count;  // Deny request (limit exceeded), keep count unchanged
    });
    
    return allowed.get();  // Return whether the request was allowed
}
}
