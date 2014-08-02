package com.sobag.parsetemplate.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.User;
import com.sobag.parsetemplate.domain.Waypoint;
import com.sobag.parsetemplate.util.PreferenceProps;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.util.SharedPreferencesUtility;

import java.util.List;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * Parse initialization related stuff...
 */
@ContextSingleton
public class ParseInitializationService
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    @Inject
    InitializationListener initializationListener;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public ParseInitializationService(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * init parse...
     */
    public void init()
    {
        initializationListener.handleStartInitialization();

        String applicationID = contextProvider.get().getString(R.string.applicationID);
        String clientID = contextProvider.get().getString(R.string.clientKey);

        // register subclasses...
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Board.class);
        ParseObject.registerSubclass(Ride.class);
        ParseObject.registerSubclass(Waypoint.class);

        // actually parse does not provide a way of checking whether initialization failed
        // or not :( - so parse will always be initialized successfully right here...
        Parse.initialize(contextProvider.get(), applicationID, clientID);
        Ln.v("Parse initialized successfully!");

        // init parse facebook_256 stuff...
        String fbAppID = contextProvider.get().getString(R.string.fbAppID);
        ParseFacebookUtils.initialize(fbAppID);

        // trigger parse request in order to make sure parse connection is working
        fetchDummyObject();
    }

    /**
     * Get current user
     * @return
     * ParseUser in case user is logged in, else null will be returned.
     */
    public User getCurrentUser()
    {
        return (User)ParseUser.getCurrentUser();
    }

    public boolean checkIfFirstTimeAccess()
    {
        return new SharedPreferencesUtility(contextProvider.get()).checkIfFirstTimeAccess();
    }

    // ------------------------------------------------------------------------
    // dummy helper
    // ------------------------------------------------------------------------

    /**
     * Fetch dummy object from parse...method can be used in order to check
     * parse connectivity
     */
    public void fetchDummyObject()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    initializationListener.handleSuccessfulInitialization();
                }
                else
                {
                    initializationListener.handleErrorDuringInitialization(e);
                }
            }
        });
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
