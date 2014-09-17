package com.sobag.parsetemplate.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.sobag.parsetemplate.LaunchActivity;
import com.sobag.parsetemplate.util.SharedPreferencesUtility;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;

/**
 * Parse initialization related stuff...
 */
@ContextSingleton
public class ParseLoginService
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public ParseLoginService(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Perform Facebook login or signup dependent on whether user already
     * signed up via FB...
     */
    public void loginOrSignupWithFacebook(final LoginListener listener)
    {
        ParseFacebookUtils.logIn((Activity) listener, new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null)
                {
                    listener.handleLoginError(err);
                }
                else
                {
                    listener.handleSuccessfulLogin();

                    // user still might be virgin user...
                    SharedPreferencesUtility sharedPrefsUtility = new SharedPreferencesUtility(contextProvider.get());
                    if (sharedPrefsUtility.checkIfFirstTimeAccess())
                    {
                        sharedPrefsUtility.unvirginUser();
                    }
                }
            }
        });
    }

    /**
     * Login...
     * @param username
     * @param password
     */
    public void login(final LoginListener listener,
                      String username, String password)
    {
        ParseUser.logInInBackground(username,password, new LogInCallback()
        {
            @Override
            public void done(ParseUser parseUser, ParseException e)
            {
                if (e == null)
                {
                    listener.handleSuccessfulLogin();
                }
                else
                {
                    listener.handleLoginError(e);
                }
            }
        });
    }

    /**
     * Logout user...
     */
    public void logout(Activity callingActivity)
    {
        ParseUser.logOut();

        Intent launchActivityIntent = new Intent(contextProvider.get(), LaunchActivity.class);
        launchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contextProvider.get().startActivity(launchActivityIntent);

        callingActivity.finish();
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------
}
