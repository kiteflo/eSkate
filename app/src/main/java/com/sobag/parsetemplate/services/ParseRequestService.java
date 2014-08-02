package com.sobag.parsetemplate.services;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.User;
import com.sobag.parsetemplate.domain.Waypoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * Parse operations
 */
@ContextSingleton
public class ParseRequestService
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public ParseRequestService(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Fetch available board descriptions from server...
     * @param requestListener
     */
    public void fetchBoards(final RequestListener requestListener)
    {
        requestListener.handleStartRequest();

        ParseQuery<Board> query = ParseQuery.getQuery("Board");
        query.findInBackground(new FindCallback<Board>() {
            @Override
            public void done(List<Board> boards, ParseException e) {
                if (e == null) {
                    requestListener.handleRequestResult(boards);
                }
                else
                {
                    requestListener.handleParseRequestError(e);
                }
            }
        });
    }

    /**
     * Save ride to parse...
     * @param requestListener
     */
    public void saveRide(final RequestListener requestListener)
    {
        requestListener.handleStartRequest();

        final Ride ride = new Ride();
        ride.setTitle("My Test ride...");

        // all waypoints...
        JSONArray array = new JSONArray();
        for (int i=0; i<100; i++)
        {
            try
            {
                JSONObject obj = new JSONObject();
                obj.put("counter", i);
                obj.put("latitude",10+1);
                obj.put("longitude",174+1);
                array.put(obj);
            }
            catch (JSONException ex)
            {
                Ln.e(ex);
            }
        }
        ride.setWaypoints(array);

        // start & end point
        try
        {
            // add geo point...
            Waypoint wp1 = new Waypoint();
            wp1.setWaypoint(new ParseGeoPoint(10, 20));
            wp1.save();
            Waypoint wp2 = new Waypoint();
            wp2.setWaypoint(new ParseGeoPoint(44, 43));
            wp2.save();

            ride.setStartPoint(wp1);
            ride.setEndPoint(wp2);
        }
        catch (Exception ex)
        {
            Ln.e(ex);
        }

        ride.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e == null)
                {
                    User user = (User)ParseUser.getCurrentUser();
                    ride.setUser(user);
                    ride.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if (e == null)
                            {
                                Ln.i("Saved ride successfully...");
                                requestListener.handleParseRequestSuccess();
                            }
                            else
                            {
                                Ln.e(e);
                                requestListener.handleParseRequestError(e);
                            }
                        }
                    });
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
