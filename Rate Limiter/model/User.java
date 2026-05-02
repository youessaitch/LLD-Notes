package model;

import enums.UserTier;

public class User {
    private final String userId;
    private final UserTier tier;

    //constructor   
    public User(String userId, UserTier tier) {
        this.userId = userId;
        this.tier = tier;
    }

    //getters
    public String getUserId() {
        return userId;
    }
    public UserTier getTier() {
        return tier;
    }
}
