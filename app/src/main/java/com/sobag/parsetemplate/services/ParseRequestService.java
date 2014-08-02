package com.sobag.parsetemplate.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sobag.parsetemplate.crappyhelpers.Counter;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideHolder;
import com.sobag.parsetemplate.domain.RideImage;
import com.sobag.parsetemplate.domain.User;
import com.sobag.parsetemplate.domain.Waypoint;
import com.sobag.parsetemplate.util.BitmapUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
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
    public void saveRide(final RequestListener requestListener,final RideHolder rideHolder)
    {
        requestListener.handleStartRequest();

        final Ride ride = new Ride();
        ride.setTitle(rideHolder.getTitle());

        // apply map image...
        Bitmap mapImg = BitmapFactory.decodeFile(rideHolder.getMapImage());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mapImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        final ParseFile parseMapImage = new ParseFile("myfile.png",data);
        parseMapImage.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e == null)
                {
                    ride.setMapImage(parseMapImage);

                    // apply waypoints...
                    JSONArray array = new JSONArray();
                    int counter = 0;
                    for (LatLng latLng : rideHolder.getWaypoints())
                    {
                        try
                        {
                            JSONObject obj = new JSONObject();
                            obj.put("counter", counter++);
                            obj.put("latitude",latLng.latitude);
                            obj.put("longitude",latLng.longitude);
                            array.put(obj);
                        }
                        catch (JSONException ex)
                        {
                            Ln.e(ex);
                        }
                    }
                    ride.setWaypoints(array);

                    // apply start & endpoint
                    Waypoint start = new Waypoint();
                    start.setWaypoint(new ParseGeoPoint(rideHolder.getStartPosition().latitude,
                            rideHolder.getStartPosition().longitude));
                    start.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if (e == null)
                            {
                                Waypoint end = new Waypoint();
                                end.setWaypoint(new ParseGeoPoint(rideHolder.getEndPosition().latitude,
                                        rideHolder.getEndPosition().longitude));
                                end.saveInBackground(new SaveCallback()
                                {
                                    @Override
                                    public void done(ParseException e)
                                    {
                                        if (e == null)
                                        {
                                            // upload ride images...
                                            try
                                            {
                                                final Counter counter = new Counter();
                                                for (String imagePath : rideHolder.getRideImages())
                                                {
                                                    File file = new File(new URI(imagePath));
                                                    Bitmap bitmap = new BitmapUtility().getDownsampledBitmap(file, 6);
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                    byte[] data = stream.toByteArray();
                                                    ParseFile imageFile = new ParseFile("GIMME_A_NAME.png", data);
                                                    final RideImage rideImage = new RideImage();
                                                    rideImage.setRideImage(imageFile);
                                                    rideImage.saveInBackground(new SaveCallback()
                                                    {
                                                        @Override
                                                        public void done(ParseException e)
                                                        {
                                                            ride.getRideImages().add(rideImage);
                                                            counter.setCount(counter.getCount()+1);

                                                            // proceed after last image has been 100%ly uploaded...
                                                            if (counter.getCount() == rideHolder.getRideImages().size())
                                                            {
                                                                ride.saveInBackground(new SaveCallback()
                                                                {
                                                                    @Override
                                                                    public void done(ParseException e)
                                                                    {
                                                                        if (e == null)
                                                                        {
                                                                            User user = (User) ParseUser.getCurrentUser();
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
                                                                                    } else
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
                                                        }
                                                    });
                                                }
                                            }
                                            catch (IOException ex)
                                            {
                                                requestListener.handleParseRequestError(ex);
                                            }
                                            catch (URISyntaxException ex)
                                            {
                                                requestListener.handleParseRequestError(ex);
                                            }
                                        }
                                        else
                                        {
                                            Ln.e(e);
                                            requestListener.handleParseRequestError(e);
                                        }
                                    }
                                });
                            } else
                            {
                                Ln.e(e);
                                requestListener.handleParseRequestError(e);
                            }
                        }
                    });
                }
                else
                {
                    Ln.e(e);
                    requestListener.handleParseRequestError(e);
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
