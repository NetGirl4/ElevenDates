package com.elevenfifty.www.elevendates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.elevenfifty.www.elevendates.Models.DateUser;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LoginActivity extends Activity {
    // bind our button here
    @Bind(R.id.facebook_button)
    Button facebookButton;

    // create a class variable for a Progress Dialog
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // super important line of code goes here
        ButterKnife.bind(this);

        // Create our dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Authentication");
        dialog.setCancelable(false);

        // Make a listener for out button
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                final ArrayList<String> permissions = new ArrayList<String>();
                permissions.add("user_about_me");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null && parseUser != null) {

                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation().getCurrentInstallation();
                                    installation.put("user", DateUser.getCurrentUser());
                                    installation.saveInBackground();

                                    GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                                    DateUser currentUser = (DateUser) DateUser.getCurrentUser();
                                                    if (currentUser.isNew()) {
                                                        currentUser.setDiscoverable(true);
                                                    }

                                                    currentUser.setFacebookId(jsonObject.optString("id"));
                                                    currentUser.setFirstName(jsonObject.optString("first_name"));
                                                    currentUser.setLastName(jsonObject.optString("last_name"));
                                                    currentUser.setName(jsonObject.optString("name"));

                                                    String pictureURL = "https://graph.facebook.com/" +
                                                            currentUser.getFacebookId() +
                                                            "/picture?type=square&width=600&height=600";

                                                    currentUser.setImage(pictureURL);
                                                    currentUser.saveInBackground();

                                                    dialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).executeAsync();

                                }   else {
                                    dialog.hide();
                                }
                            }
                        });
            }
        });
    }

    // We need to use onActivityResult to get what Facebook sends
    // back to us


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
