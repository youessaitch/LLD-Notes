package service;

import java.util.Map;
import java.util.HashMap;

import limiter.RateLimiter;
import enums.RateLimitType;
import enums.UserTier;
import factory.RateLimiterFactory;
import model.RateLimitConfig;
import model.User;

public class RateLimiterService {
    private final Map<UserTier, RateLimiter> rateLimiters = new HashMap<>(); //Tier → their assigned rate limiter

    //constructor to initialize rate limiters for each user tier
    public RateLimiterService(){
        rateLimiters.put(UserTier.FREE, RateLimiterFactory.createRateLimiter(RateLimitType.TOKEN_BUCKET, new RateLimitConfig(10, 60))); // Free users: 10 req/min
        rateLimiters.put(UserTier.PREMIUM, RateLimiterFactory.createRateLimiter(RateLimitType.FIXED_WINDOW, new RateLimitConfig(100, 60))); // Premium users: 100 req/min
    }

    public boolean allowRequest(User user){
        RateLimiter limiter = rateLimiters.get(user.getTier()); // Get the appropriate rate limiter based on user's tier
        if (limiter == null) {
            throw new IllegalArgumentException("No rate limiter found for user tier: " + user.getTier());
        }
        return limiter.allowRequest(user.getUserId());
    }
}

//Notes : 
// Limits are shared across all users in a tier, so if one free user makes 10 requests, all free users will be blocked until the window resets. In a real system, we might want to have separate limits per user or use a more sophisticated approach.

// FULL FLOW : User → Service → Factory → Strategy → Result

// DESIGN PATTERNS:
// 1. Strategy Pattern: RateLimiter is the strategy interface, and TokenBucketRateLimiter and FixedWindowRateLimiter are concrete strategies. This allows us to easily add new rate limiting algorithms in the future without changing the existing code.
// 2. Factory Pattern: RateLimiterFactory abstracts away the instantiation logic for different rate limiters. The service doesn't need to know the details of how each limiter is created, it just asks the factory for the appropriate limiter based on the type. This promotes loose coupling and adheres to the Open/Closed Principle, allowing us to add new limiter types without modifying existing code.

// Service Layer : RateLimiterService → orchestrates usage
