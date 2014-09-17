package com.sobag.parsetemplate.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sobag.parsetemplate.CommonActivity;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.ClientUser;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideImage;
import com.sobag.parsetemplate.domain.User;
import com.sobag.parsetemplate.domain.Waypoint;
import com.sobag.parsetemplate.util.PreferenceProps;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.util.SharedPreferencesUtility;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    @Inject
    ClientUser clientUser;

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
        ParseObject.registerSubclass(RideImage.class);

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

    // init client user
    public void initClientUser(ProfilePictureView fbImageView)
    {
        if (clientUser == null)
        {
            clientUser = new ClientUser();
        }

        // facebook user?
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened())
        {
            makeMeRequest(fbImageView);
        }
        else
        {
            clientUser.setFirstname("TO");
            clientUser.setLastname("DO");
        }
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

    private void makeMeRequest(final ProfilePictureView fbImageView) {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {
                        if (user != null)
                        {
                            clientUser.setFirstname(user.getFirstName());
                            clientUser.setLastname((user.getLastName()));
                            clientUser.setFacebookID(user.getId());

                            fbImageView.setProfileId(user.getId());
                        }
                    }
                });
        request.executeAsync();

    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    public class DownloadImagesTask extends AsyncTask<ClientUser, Void, Bitmap>
    {
        private ClientUser clientUser;
        private String url;

        public DownloadImagesTask(String url)
        {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(ClientUser... clientUser) {
            this.clientUser = clientUser[0];
            return download_Image(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            clientUser.setUserImage(result);
        }

        private Bitmap download_Image(String url)
        {
            HttpURLConnection urlConnection = null;
            HttpURLConnection.setFollowRedirects(true);
            Bitmap bmp =null;

            try
            {
                URL urln = new URL(url);
                urlConnection = (HttpURLConnection) urln.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bmp = BitmapFactory.decodeStream(in);
                if (bmp != null)
                {
                    return bmp;
                }
            }
            catch (Exception ex)
            {
                Ln.e(ex);
            }

            return bmp;
        }
    }

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------
}
