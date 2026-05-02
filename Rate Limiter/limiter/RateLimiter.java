package limiter;

import enums.RateLimitType;
import model.RateLimitConfig;

public abstract class RateLimiter {
    protected final RateLimitConfig config; //accessible to subclasses so protected , final -> once set , cannot change
    protected final RateLimitType type;

    public RateLimiter(RateLimitConfig config, RateLimitType type){ //constructor
        this.config = config;
        this.type = type;
    }

    public abstract boolean allowRequest(String userId); //core method -> each subclasses will implement this
}


// ^ this is Strategy Pattern since RateLimiter = strategy interface/base and subclasses = different strategies

// Why abstract instead of Interface ?
// Ans -> Because we have shared data (config and type), Interfaces can't hold state (properly).
// “Because all rate limiters share common configuration and type, so I use an abstract class to avoid duplication and enforce a common contract.”