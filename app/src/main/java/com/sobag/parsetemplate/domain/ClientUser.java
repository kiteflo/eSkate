package com.sobag.parsetemplate.domain;

import android.graphics.Bitmap;

import com.google.inject.Singleton;

/**
 * Created by tzhmufl2 on 04.09.14.
 */
@Singleton
public class ClientUser
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String facebookID = null;
    private String firstname;
    private String lastname;
    private Bitmap userImage;

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public Bitmap getUserImage()
    {
        return userImage;
    }

    public void setUserImage(Bitmap userImage)
    {
        this.userImage = userImage;
    }

    public String getFacebookID()
    {
        return facebookID;
    }

    public void setFacebookID(String facebookID)
    {
        this.facebookID = facebookID;
    }
}
