package com.sobag.parsetemplate.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.User;
import com.sobag.parsetemplate.util.PreferenceProps;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;

/**
 * Parse initialization related stuff...
 */
@ContextSingleton
public class ParseSignupService
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public ParseSignupService(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Signup at parse using the given credentials...
     * @param username
     * @param password
     * @param email
     * @param listener
     */
    public void signupViaEmail(String username, String password, String email,
                               final SignupListener listener)
    {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);

        user.signUpInBackground(new SignUpCallback()
        {
            public void done(ParseException e)
            {
                if (e == null)
                {
                    listener.handleSuccessfulSignup();
                    unvirginUser();
                }
                else
                {
                    listener.handleSignupError(e);
                }
            }
        });
    }

    /**
     * Perform Facebook login or signup dependent on whether user already
     * signed up via FB...
     */
    public void loginOrSignupWithFacebook(final SignupListener listener)
    {
        ParseFacebookUtils.logIn((Activity) listener, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    listener.handleSignupError(err);
                } else if (user.isNew()) {
                    listener.handleSuccessfulSignup();
                    unvirginUser();
                } else {
                    listener.handleSuccessfulSignup();
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    /**
     * Make this user a real man...by remnoving the virgin signup flag!
     */
    private void unvirginUser()
    {
        String filename = contextProvider.get().getString(R.string.preferencesFile);
        SharedPreferences preferences = contextProvider.get().getSharedPreferences(filename,
                Context.MODE_PRIVATE);

        preferences.edit().putBoolean(PreferenceProps.IS_VIRGIN,false).commit();
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------
}
