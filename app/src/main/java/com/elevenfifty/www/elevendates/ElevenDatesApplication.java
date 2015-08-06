package com.elevenfifty.www.elevendates;

import android.app.Application;
import android.util.Log;

import com.elevenfifty.www.elevendates.Models.DateChat;
import com.elevenfifty.www.elevendates.Models.DateMatch;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 *
 * Created by bkeck on 7/6/15.
 *
 */
public class ElevenDatesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        //Register subclasses
        ParseUser.registerSubclass(DateUser.class);
        ParseObject.registerSubclass(DateMatch.class);
        ParseObject.registerSubclass(DateChat.class);

        Parse.initialize(this, "NYtQNVIjIIIqcfKeIHBmP8oyxsSRFMDGuDLkqjST", "RVZyItsT2py9SFj8WZOZzySAB4Ke8ia9LhlXUlhq");
        ParseFacebookUtils.initialize(this);

        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        ArrayList<String> channels = new ArrayList<>();
        channels.add("global");
        parseInstallation.put("channels", channels);
        parseInstallation.saveInBackground();

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
