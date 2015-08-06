package com.elevenfifty.www.elevendates;

import com.elevenfifty.www.elevendates.Models.DateMatch;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by kathy on 7/15/2015.
 */
public class DataStore {
    private static DataStore ourInstance = new DataStore();

    private DateUser otherUser;

    public static DataStore getInstance() {
        return ourInstance;
    }

    private DataStore() {
    }

    public void likePerson(DateUser user) {
        matchUser((DateUser)DateUser.getCurrentUser(), user, true);
    }

    public void nopePerson(DateUser user) {
        matchUser((DateUser)DateUser.getCurrentUser(), user, false);
    }

    private void matchUser(DateUser user1, DateUser user2,
                           boolean isMatch) {
        DateMatch match = new DateMatch();
        match.setCurrentUser(user1);
        match.setTargetUser(user2);
        match.setIsMatch(isMatch);

        checkMatch(match);
    }

    private void checkMatch(final DateMatch match) {
        if (match.getIsMatch()) {
            ParseQuery<DateMatch> matchQuery = ParseQuery.getQuery(DateMatch.class);
            matchQuery.whereEqualTo(Constants.CURRENT_USER, match.getTargetUser());
            matchQuery.whereEqualTo(Constants.TARGET_USER, match.getCurrentUser());
            matchQuery.whereEqualTo(Constants.IS_MATCH, true);

            matchQuery.getFirstInBackground(new GetCallback<com.elevenfifty.www.elevendates.Models.DateMatch>() {
                @Override
                public void done(DateMatch dateMatch, ParseException e) {
                    boolean mutualMatch = false;
                    if (dateMatch != null) {
                        mutualMatch = true;
                        dateMatch.setMutualMatch(mutualMatch);
                        dateMatch.saveInBackground();
                    }
                    updateOrInsertMatch(match, mutualMatch);
                }
            });
        } else {

            updateOrInsertMatch(match, false);
        }
    }

    private void updateOrInsertMatch(final DateMatch match, final boolean mutualMatch) {
        ParseQuery<DateMatch> matchQuery = ParseQuery.getQuery(DateMatch.class);
        matchQuery.whereEqualTo(Constants.CURRENT_USER, match.getCurrentUser());
        matchQuery.whereEqualTo(Constants.TARGET_USER, match.getTargetUser());
        matchQuery.getFirstInBackground(new GetCallback<DateMatch>() {
            @Override
            public void done(DateMatch dateMatch, ParseException e) {
                if (dateMatch != null) {
                    dateMatch.setIsMatch(match.getIsMatch());
                    dateMatch.setMutualMatch(mutualMatch);
                    dateMatch.saveInBackground();
                } else {
                    match.setMutualMatch(mutualMatch);
                    match.saveInBackground();
                }
            }
        });

    }


    public DateUser getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(DateUser otherUser) {
        this.otherUser = otherUser;
    }
}
