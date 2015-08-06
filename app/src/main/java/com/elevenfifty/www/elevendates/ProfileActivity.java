package com.elevenfifty.www.elevendates;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.elevenfifty.www.elevendates.Models.DatePreference;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ProfileActivity extends AppCompatActivity {
    // I'll give you some of these, so we don't have broken
    // code down below
    @Bind(R.id.genderGroup)
    SegmentedGroup genderGroup;

    @Bind(R.id.ageBar)
    SeekBar ageBar;

    @Bind(R.id.discoverable)
    Switch discoverable;


    // It seems, though, like we should "bind" ourselves to some
    // more layout objects here...(4 more)


    @Bind(R.id.displayName)
    TextView displayName;

    @Bind(R.id.photoImage)
    ImageView photoImage;

    @Bind(R.id.ageLabel)
    TextView ageLabel;

    @Bind(R.id.genderPrefGroup)
    SegmentedGroup genderPrefGroup;



    // These are our private variables, we will use them.
    // Seriously - we'll, like, use every one of them
    private String gender;
    private int age;
    private boolean disc;
    private DatePreference show;  //<-- UNCOMMENT THIS LINE!!!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // A key line needs to go here, things will break if we
        // don't have it!
        ButterKnife.bind(this);

        // I'll give you this instantiation for free (donations accepted)
        DateUser currentUser = (DateUser)DateUser.getCurrentUser();

        // We need to set up our view here by populating our
        // layout items with data from our currentUser
        // let's start at the top and work our way down
        // name:
        displayName.setText(currentUser.getName());


        // gender:
        gender = currentUser.getGender();
        if (gender.equals(Constants.MALE)) {
            genderGroup.check(R.id.genderMale);
        } else {
            genderGroup.check(R.id.genderFemale);
        }

        // profile image(hint - Picasso is your friend):
        Picasso.with(this).load(currentUser.getImage())
                .placeholder(R.mipmap.profile_placeholder)
                .error(R.mipmap.ohohno).into(photoImage);


        // discoverable:
        disc = currentUser.isDiscoverable();
        discoverable.setChecked(disc);

        // age:
        age = currentUser.getAge();
        ageLabel.setText(String.valueOf(age));
        ageBar.setProgress(age - 21);

        // gender preference:
        show = currentUser.getShow();
        switch (show) {
            case MALE_ONLY:
                genderPrefGroup.check(R.id.genderPrefGroup);
                break;
            case FEMALE_ONLY:
                genderPrefGroup.check(R.id.genderPrefFemale);
                break;
            case BOTH:
                genderPrefGroup.check(R.id.genderPrefBoth);
                break;
        }


        // we are going to set up listeners for a few
        // of our objects in this next method
        setupListeners();
    }

    private void setupListeners() {
        // we want to know when a user has changed the Gender
        // selection
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // figure out if the checkedId is Male or Female,
                // adjust your gender variable accordingly
                if (checkedId == R.id.genderMale) {
                    gender = Constants.MALE;
                } else {
                    gender = Constants.FEMALE;
                }
            }
        });

        // we want to know if the user has changed their
        // discoverable setting
        discoverable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // assign change to disc variable
                disc = isChecked;
            }
        });

        // we want to know if the age seekbar has been slid.  Slided?
        // Slidered?  Slud?  Sliderededed?
        ageBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // change the ageLabel and set the age variable
                age = progress + 21;
                ageLabel.setText(String.valueOf(age));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to see here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
                // I see you thinking about doing something here anyway
                // Don't do it, seriously
                // Bunnies will cry if you do anything here
                // And you don't want that
                // Not because that's sad, but because sad bunnies
                // are REALLY loud
            }
        });

        // we want to know when a user has changed the
        // Gender Preference selection
        genderPrefGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.genderFemale) {
                    show = DatePreference.MALE_ONLY;
                } else if (checkedId == R.id.genderPrefFemale) {
                    show = DatePreference.FEMALE_ONLY;
                } else {
                    show = DatePreference.BOTH;
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            // Get the current user
            DateUser currentUser = (DateUser) DateUser.getCurrentUser();


            // Update all the variables of the current user that can
            // be changed on this page
            currentUser.setGender(gender);
            currentUser.setDiscoverable(disc);
            currentUser.setShow(show);
            currentUser.setAge(age);


            // Save the user (in the background would be nice).  When
            // the save finishes, tell the user whether or not it
            // worked.  A toast will do just fine here (hint: google
            // "how to display a toast in android?" if you don't know
            // what that means
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ProfileActivity.this,
                                getString(R.string.profile_saved),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                getString(R.string.error) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}