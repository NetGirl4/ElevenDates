package com.elevenfifty.www.elevendates.Models;

import com.elevenfifty.www.elevendates.Constants;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 *
 * Created by bkeck on 7/6/15.
 *
 */

@ParseClassName("DateMatch")
public class DateMatch extends ParseObject {
    // I'd like two DateUser variables for the currentUser and
    // targetUser, a boolean for isMatch, a boolean for mutualMatch,
    // and a side of fries.  And do you have Shamrock Shakes right now?
    private DateUser currentUser;
    private DateUser targetUser;
    private Boolean isMatch;
    private Boolean mutualMatch;


    // Time for some Getter and Setter Action, folks
    // Might want to use the Constants file for strings, here

    public DateUser getCurrentUser() {
        return (DateUser)get(Constants.CURRENT_USER);
    }

    public void setCurrentUser(DateUser currentUser) {
        put(Constants.CURRENT_USER, currentUser);

    }

    public DateUser getTargetUser() {
        return (DateUser)get(Constants.TARGET_USER);
    }

    public void setTargetUser(DateUser targetUser) {
        put(Constants.TARGET_USER, targetUser);
    }

    public Boolean getIsMatch() {
        return getBoolean(Constants.IS_MATCH);
    }

    public void setIsMatch(Boolean isMatch) {
        put(Constants.IS_MATCH, isMatch);
    }

    public Boolean getMutualMatch() {
        return getBoolean(Constants.MUTUAL_MATCH);
    }

    public void setMutualMatch(Boolean mutualMatch) {
        put(Constants.MUTUAL_MATCH, mutualMatch);
    }
}
