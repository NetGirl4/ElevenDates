package com.elevenfifty.www.elevendates.Models;

import com.elevenfifty.www.elevendates.Constants;
import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 *
 * Created by bkeck on 12/27/14.
 *
 */

@ParseClassName("_User")
public class DateUser extends ParseUser {
    private String facebookId;
    private String firstName;
    private String lastName;
    private String name;
    private String image;
    private Boolean discoverable;
    // I might want some more variables here, to hold the user's age,
    // gender, and preference of what gender to show
    private Integer age;
    private String gender;
    private DatePreference show;


    public String getFacebookId() {
        return getString(Constants.FACEBOOK_ID);
    }

    public void setFacebookId(String facebookId) {
        put(Constants.FACEBOOK_ID, facebookId);
    }

    public String getFirstName() {
        return getString(Constants.FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(Constants.FIRST_NAME,firstName);
    }

    public String getLastName() {
        return getString(Constants.LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(Constants.LAST_NAME,lastName);
    }

    public String getName() {
        return getString(Constants.NAME);
    }

    public void setName(String name) {
        put(Constants.NAME,name);
    }

    public String getImage() {
        return getString(Constants.IMAGE);
    }

    public void setImage(String image) {
        put(Constants.IMAGE,image);
    }

    public Boolean isDiscoverable() {
        return getBoolean(Constants.DISCOVERABLE);
    }

    public void setDiscoverable(Boolean discoverable) {
        put(Constants.DISCOVERABLE,discoverable);
    }

    // You know, I added those other class variables above, some
    // getters and setters would be nice

    public Integer getAge() {
        return get(Constants.AGE) == null ?
                21 : getInt(Constants.AGE);
    }

    public void setAge(Integer age) {
        put(Constants.AGE, age);
    }

    public String getGender() {
        return get(Constants.GENDER) == null ? Constants.MALE :
                getString(Constants.GENDER);
    }

    public void setGender(String gender) {
        put(Constants.GENDER, gender);
    }

    public DatePreference getShow() {
        return get(Constants.SHOW) == null ? DatePreference.BOTH :
                DatePreference.values()[getInt(Constants.SHOW)];
    }

    public void setShow(DatePreference show) {
        put(Constants.SHOW, show.getValue());
    }


    // At some point, I might want a handy dandy function to show the
    // first name and the age of this person
    public String displayText() {
        return getFirstName() + ", " + getAge();
    }
}
