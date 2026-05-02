package factory;

import enums.RateLimitType;
import model.RateLimitConfig;
import limiter.FixedWindowRateLimiter;
import limiter.RateLimiter;
import limiter.TokenBucketRateLimiter;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimitType type, RateLimitConfig config){ //Returning RateLimiter (abstract)
        return switch(type){
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(config);
            case FIXED_WINDOW -> new FixedWindowRateLimiter(config);
            default -> throw new IllegalArgumentException("Unsupported rate limiter type: " + type);
        };
    }
}
