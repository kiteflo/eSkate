package com.sobag.parsetemplate.fb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.parse.ParseFacebookUtils;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.RideHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import roboguice.util.Ln;

/**
 * Created by tzhmufl2 on 14.08.14.
 */
public class FacebookHandler
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private static final String POST_ACTION_PATH = "me/eskateapp:ride";
    static final boolean UPLOAD_IMAGE = true;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void shareRideOnFacebook(RideHolder rideHolder)
    {
        // Create a batch request
        RequestBatch requestBatch = new RequestBatch();

        // If uploading an image, set up the first batch request
        // to do this.
        if (UPLOAD_IMAGE) {
            // Set up image upload request parameters
            Bitmap image = BitmapFactory.decodeFile(rideHolder.getMapImage());

            // Set up the image upload request callback
            Request.Callback imageCallback = new Request.Callback() {

                @Override
                public void onCompleted(Response response) {
                    // Log any response error
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Ln.i(error.getErrorMessage());
                    }
                }
            };

            // Create the request for the image upload
            Request imageRequest = Request.newUploadStagingResourceWithImageRequest(Session.getActiveSession(),
                    image, imageCallback);

            // Set the batch name so you can refer to the result
            // in the follow-on object creation request
            imageRequest.setBatchEntryName("imageUpload");

            // Add the request to the batch
            requestBatch.add(imageRequest);
        }

        // Request: Object request
        // --------------------------------------------

        // create image
        String imageUrl = UPLOAD_IMAGE ? "{result=imageUpload:$.uri}" :
                "https://dl.dropboxusercontent.com/u/33720504/temp/aramis.jpeg";
        GraphObject imageObject = GraphObject.Factory.create();
        imageObject.setProperty("url", imageUrl);
        if (UPLOAD_IMAGE) {
            imageObject.setProperty("user_generated", "true");
        }
        GraphObjectList<GraphObject> images = GraphObject.Factory.createList(GraphObject.class);
        images.add(imageObject);

        // Set up the OpenGraphObject representing the book.
        OpenGraphObject track = OpenGraphObject.Factory.createForPost("eskateapp:track");
        track.setImage(images);
        track.setTitle(rideHolder.getTitle());
        track.setDescription(rideHolder.getDuration());
        // books.book-specific properties go under "data"

        // Set up the object request callback
        Request.Callback objectCallback = new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Log any response error
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Ln.i(error.getErrorMessage());
                }
            }
        };

        // Create the request for object creation
        Request objectRequest = Request.newPostOpenGraphObjectRequest(Session.getActiveSession(),
                track, objectCallback);

        // Set the batch name so you can refer to the result
        // in the follow-on publish action request
        objectRequest.setBatchEntryName("objectCreate");

        // Add the request to the batch
        requestBatch.add(objectRequest);

        // TO DO: Add the publish action request to the batch

        // Request: Publish action request
        // --------------------------------------------
        OpenGraphAction rideAction = OpenGraphAction.Factory.createForPost("eskateapp:ride");
        // Refer to the "id" in the result from the previous batch request
        rideAction.setProperty("track", "{result=objectCreate:$.id}");
        rideAction.setExplicitlyShared(true);

        // Set up the action request callback
        Request.Callback actionCallback = new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Ln.e(error.getErrorMessage());
                } else {
                    String actionId = null;
                    try {
                        JSONObject graphResponse = response
                                .getGraphObject()
                                .getInnerJSONObject();
                        actionId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Ln.i("JSON error " + e.getMessage());
                    }
                    Ln.i(actionId);
                }
            }
        };

        // Create the publish action request
        Request actionRequest = Request.newPostOpenGraphActionRequest(Session.getActiveSession(),
                rideAction, actionCallback);

        // Add the request to the batch
        requestBatch.add(actionRequest);

        // Execute the batch request
        requestBatch.executeAsync();
    }
}
