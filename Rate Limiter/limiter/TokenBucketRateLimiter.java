package limiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import enums.RateLimitType;
import model.RateLimitConfig;

public class TokenBucketRateLimiter extends RateLimiter {
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>(); //userId → current number of tokens
    private final Map<String, Long> lastRefillTime = new HashMap<>(); //userId → last time tokens were refilled

    public TokenBucketRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.TOKEN_BUCKET); //call parent constructor to set config and type
    }

    @Override
    public boolean allowRequest(String userId){
        AtomicBoolean allowed = new AtomicBoolean(false);  // Thread-safe flag to track if request is allowed
        long currTime = System.currentTimeMillis() / 1000;

        tokens.compute(userId,(id,availableToken)->{
            int currTokens = refillTokens(userId,currTime);
            if(currTokens > 0){
                allowed.set(true); // Allow the request
                return currTokens - 1; // Consume a token
            }
            return currTokens; // Deny request (no tokens available), keep token count unchanged
        });
        return allowed.get();
    }

    private int refillTokens(String userId, long currTime){
        double refillRate = (double) config.getTimeWindowInSeconds() / config.getMaxRequests(); // Calculate how many seconds it takes to generate one token

        long lastRefill = lastRefillTime.getOrDefault(userId, currTime); // Get the last refill time for this user, default to current time if not present

        long elapsedTime = currTime - lastRefill; // Calculate how much time has passed since last refill
        int refillTokens = (int) (elapsedTime / refillRate); // Calculate how many tokens to add based on elapsed time and refill rate

        int currentTokens = tokens.getOrDefault(userId, config.getMaxRequests()); // Get current tokens, default to max if not present
        int newTokenCount = Math.min(currentTokens + refillTokens, config.getMaxRequests()); // Update token count, ensuring it does not exceed max

        if(refillTokens > 0){
            lastRefillTime.put(userId, currTime); // Update last refill time to current time
        }

        return newTokenCount; // Return the updated token count
    }
}
