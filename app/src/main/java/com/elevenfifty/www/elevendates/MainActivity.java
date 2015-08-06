package com.elevenfifty.www.elevendates;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.xbutton)
    ImageButton xButton;
    @Bind(R.id.heartbutton)
    ImageButton heartButton;

    // Need to bind a lot of things here
    @Bind(R.id.card_area)
    ViewGroup cardarea;

    @Bind(R.id.user_card)
    ViewGroup userCard;
    @Bind(R.id.pass_image)
    ImageView passImage;
    @Bind(R.id.fail_image)
    ImageView failImage;
    @Bind(R.id.user_image)
    ImageView userImage;
    @Bind(R.id.user_name_age)
    TextView userNameAge;

    @Bind(R.id.preview_user_card)
    ViewGroup previewUserCard;
    @Bind(R.id.preview_user_image)
    ImageView previewUserImage;
    @Bind (R.id.preview_user_name_age)
    TextView previewUserNameAge;



    private TranslateAnimation failAnimation;
    private TranslateAnimation passAnimation;
    private Animation.AnimationListener resetListener;

    private View.OnTouchListener touchListener;
    private int prevX;
    private int prevY;

    private int skip = 0;
    private DateUser[] potentialMatches;
    private int nextPreview = 0;
    private DateUser currentPotential;
    private DateUser nextPotential;
    private boolean actionLock = false;

    // Once we create a datastore, let's get an instance here
    private DataStore dataStore = DataStore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // I don't know if you know this, but this call is kind
        // of a big deal.  It has many leather-bound books and
        // smells of rich mahogany.
        ButterKnife.bind(this);

        loading();

        setupButtons();
        setupAnimations();
        setupTouchListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // I don't know about all of you, but this seems like a really
        // fun place to see if we have a user logged in.  If we do,
        // then let's load some cards.  If we don't, then let's take
        // them to the login screen
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            loadCards(true);
        }

    }

    // What should we do every time we are loading? Clean house?
    private void loading() {
        passImage.setAlpha(0.0f);
        failImage.setAlpha(0.0f);
        userImage.setImageBitmap(null);
        userNameAge.setText("");
        previewUserImage.setImageBitmap(null);
        previewUserNameAge.setText("");
    }

    // load some cards on up in this here piece
    private void loadCards(final boolean reset) {
        if (reset) {
            loading();
            skip = 0;

        }

        DateUser currentUser = (DateUser)DateUser.getCurrentUser();
        ParseQuery<DateUser> query = ParseQuery.getQuery(DateUser.class);
        query.setSkip(skip);
        switch (currentUser.getShow()) {
            case BOTH:
                break;
            case FEMALE_ONLY:
                query.whereEqualTo(Constants.GENDER, Constants.FEMALE);
                break;
            case MALE_ONLY:
                query.whereEqualTo(Constants.GENDER, Constants.MALE);
                break;
        }
        query.whereNotEqualTo(Constants.OBJECT_ID, currentUser.getObjectId());
        query.whereEqualTo(Constants.DISCOVERABLE, true);
        query.findInBackground(new FindCallback<DateUser>() {
            @Override
            public void done(List<DateUser> list, ParseException e) {
                if (list.size() > 0) {
                    Log.d("test", "list.size(): " + list.size());
                    skip += list.size();
                    potentialMatches = list.toArray(new DateUser[list.size()]);
                    nextPreview = 0;
                    if (reset) {
                        firstLoad();
                    }
                }
            }
        });
    }

    // Let's do our first load here
    private void firstLoad() {
        if (potentialMatches.length == 0) {
            return;
        }

        currentPotential = potentialMatches[0];
        userNameAge.setText(currentPotential.displayText());
        Picasso.with(this).load(currentPotential.getImage())
                .placeholder(R.mipmap.profile_placeholder)
                .error(R.mipmap.ohohno).into(userImage);
        nextPreview = 1;
        userCard.setOnTouchListener(touchListener);
        loadPreview();

    }

    // Wanna see what's next?
    private void loadPreview() {
        if (nextPreview >= potentialMatches.length) {
            nextPotential = null;
            previewUserImage.setImageDrawable(getResources().getDrawable(R.mipmap.closed));
            return;
        }
        nextPotential = potentialMatches[nextPreview];

        previewUserNameAge.setText(nextPotential.displayText());
        Picasso.with(this).load(nextPotential.getImage())
                .placeholder(R.mipmap.profile_placeholder)
                .error(R.mipmap.ohohno).into(previewUserImage);

        if (++nextPreview >= potentialMatches.length) {
            loadCards(false);
        }

    }

    // Moving on to the next user
    private void nextUserCard() {
        // 1: Replace current user card with preview card
        currentPotential = nextPotential;
        userNameAge.setText(previewUserNameAge.getText());
        userImage.setImageDrawable(previewUserImage.getDrawable());
        if (currentPotential == null) {
            userCard.setOnTouchListener(null);
        } else {
            userCard.setOnTouchListener(touchListener);
        }
        // 2: Reset current user card to center
        failImage.setAlpha(0.0f);
        passImage.setAlpha(0.0f);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)userCard.getLayoutParams();
        params.leftMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.bottomMargin = 0;
        userCard.setLayoutParams(params);



        // 3: Erase preview card contents
        previewUserImage.setImageBitmap(null);
        previewUserNameAge.setText("");


        // 4: Unlock the action and load up a preview
        actionLock = false;
        loadPreview();

    }

    // Mikey likes it!
    private void doYes() {
        if (currentPotential == null) {
            return;
        }
        actionLock = true;
        dataStore.likePerson(currentPotential);
        passImage.setAlpha(1.0f);
        userCard.startAnimation(passAnimation);
    }

    // No, I do not like
    private void doNo() {
        if (currentPotential == null) {
            return;
        }
        actionLock = true;
        dataStore.nopePerson(currentPotential);
        failImage.setAlpha(1.0f);
        userCard.startAnimation(failAnimation);
    }


    private void setupButtons() {
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!actionLock) {
                    // What do we do when the no button is tapped?
                    doNo();

                }
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!actionLock) {
                    // What do we do when the yes button is tapped?
                    doYes();
                }
            }
        });
    }

    private void setupAnimations() {
        // Get the size of the screen on our device so we can use
        // it's width in calculations
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;


        // Instantiate our fail animation, and set a duration
        failAnimation = new TranslateAnimation(0, -screenWidth - 100, 0, -200);
        failAnimation.setDuration(500);

        // Instantiate our pass animation, and set a duration
        passAnimation = new TranslateAnimation(0, screenWidth + 100, 0, -200);
        passAnimation.setDuration(500);

        // create a new Animation Listener so we can perform
        // operations after our animations complete
        TranslateAnimation.AnimationListener animationListener =
                new TranslateAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // We need to set up the next user card at this point
                        xButton.setSoundEffectsEnabled(true);
                        heartButton.setSoundEffectsEnabled(true);
                        nextUserCard();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };

        // Attach our Animation Listener to our pass and fail
        // animations
        failAnimation.setAnimationListener(animationListener);
        passAnimation.setAnimationListener(animationListener);

        resetListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // We need to re-enable our button sound effects
                // and the buttons themselves, and allow the user
                // to interact with the app again
                xButton.setSoundEffectsEnabled(true);
                heartButton.setSoundEffectsEnabled(true);
                actionLock = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    private void setupTouchListener() {
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                final FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams)v.getLayoutParams();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        // We need the raw location of our current event
                        prevX = (int)event.getRawX();
                        prevY = (int)event.getRawY();

                        // Disable our buttons and their sounds
                        xButton.setSoundEffectsEnabled(false);
                        heartButton.setSoundEffectsEnabled(false);
                        actionLock = true;

                        return true;
                    case MotionEvent.ACTION_UP:
                        // if we drag far enough left or right, we will
                        // pass or fail the image and call it's animation.
                        // If we don't move it far enough, we need to
                        // reset the image to its original spot
                        if (params.leftMargin < -200) {
                            // Do something negative here
                            doNo();


                        } else if (params.leftMargin > 200) {
                            // Do something positive here
                            doYes();


                        } else {
                            // We need to create a new animation here to reset
                            // our image back to its original spot
                            Animation resetParams = new Animation() {
                                @Override
                                protected void applyTransformation(float interpolatedTime, Transformation t) {
                                    // Create new params by getting the current params
                                    // and adjusting them based on their interpolated time
                                    FrameLayout.LayoutParams newParams =
                                            (FrameLayout.LayoutParams) v.getLayoutParams();
                                    params.leftMargin = (int) (params.leftMargin -
                                            (params.leftMargin * interpolatedTime));
                                    params.topMargin = (int) (params.topMargin -
                                            (params.topMargin * interpolatedTime));
                                    params.rightMargin = (int) (params.rightMargin -
                                            (params.rightMargin * interpolatedTime));
                                    params.bottomMargin = (int) (params.bottomMargin -
                                            (params.bottomMargin * interpolatedTime));

                                    // set proper visibility on our fail and pass images here,
                                    // as well as set their alpha levels accordingly
                                    float alphaValue = (float) Math.abs(params.leftMargin) / 200.0f;
                                    if (params.leftMargin >= 0){
                                        passImage.setAlpha(alphaValue);
                                    } else {
                                        passImage.setAlpha(0.0f);
                                        failImage.setAlpha(alphaValue);
                                    }


                                    // Set our new layout params for the view
                                    userCard.setLayoutParams(newParams);

                                }
                            };

                            // Set our animations duration, listener, and then start it
                            resetParams.setDuration(250);
                            resetParams.setAnimationListener(resetListener);
                            userCard.startAnimation(resetParams);

                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Create our image's layout parameters based on our location
                        params.leftMargin += (int)event.getRawX() - prevX;
                        params.topMargin += (int)event.getRawY() - prevY;
                        params.rightMargin = -params.leftMargin;
                        params.bottomMargin = -params.topMargin;

                        // save our current position to our previous
                        // position variables
                        prevX = (int)event.getRawX();
                        prevY = (int)event.getRawY();

                        // set the new layout parameters on our view
                        v.setLayoutParams(params);

                        // call the PhotoCard's setFail and setPass
                        // methods depending on which is the front card
                        if (params.leftMargin >= 0) {
                            failImage.setAlpha(0.0f);
                            passImage.setAlpha((float)Math.abs(params.leftMargin) / 200.0f);
                        } else {
                            passImage.setAlpha(0.0f);
                            failImage.setAlpha((float)Math.abs(params.leftMargin) / 200.0f);
                        }

                        return true;
                }
                return false;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.matches) {
            // We have an intent to open up our MatchesActivity
            Intent intent = new Intent(this, MatchesActivity.class);


            // Start our activity
            startActivity(intent);


            return true;
        }
        if (id == R.id.action_profile) {
            // Just what are your intentions here?
            Intent intent = new Intent(this, ProfileActivity.class);

            // This activity isn't going to start itself, you know
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_logout) {
            // Kick the current user out.  Right the heck out.
            // And send them to the LoginActivity while you're at it
            // Don't forget, finish what you've already started...

            ParseUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
