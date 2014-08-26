package com.sobag.parsetemplate;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideImage;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.fb.FacebookHandler;
import com.sobag.parsetemplate.lists.BoardListAdapter;
import com.sobag.parsetemplate.lists.RideListAdapter;
import com.sobag.parsetemplate.services.ParseRequestService;
import com.sobag.parsetemplate.services.RequestListener;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.GlobalUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class RidesActivity extends CommonActivity
        implements RequestListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Nullable
    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @Inject
    ParseRequestService parseRequestService;

    @Inject
    FontUtility fontUtility;

    @InjectView(tag = "tv_label")
    TextView tvLabel;


    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        // fetch rides...
        parseRequestService.fetchRidesForUser(this);
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void onCreateRide(View view)
    {
        Intent initRideActivity = new Intent(this,InitRideActivity.class);
        startActivity(initRideActivity);

        fontUtility.applyFontToComponent(tvLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
    }

    // ------------------------------------------------------------------------
    // request handling
    // ------------------------------------------------------------------------

    @Override
    public void handleStartRequest()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleRequestResult(List result)
    {
        progressBar.setVisibility(View.GONE);

        final List<Ride> rides = (List<Ride>)result;

        RideListAdapter rla = new RideListAdapter(getApplicationContext(),
                rides, fontUtility);

        // fetch UI container and mixin contents...
        ListView lvRides = (ListView)findViewById(R.id.lv_rides);
        lvRides.setAdapter(rla);

        lvRides.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Ride ride = rides.get(position);

                Intent rideSummaryActivity = new Intent(getApplicationContext(),RideSummaryActivity.class);
                rideSummaryActivity.putExtra("title",ride.getTitle());
                rideSummaryActivity.putExtra("date", GlobalUtility.dateFormat.format(ride.getRideDate()));
                rideSummaryActivity.putExtra("waypoints",ride.getWaypoints().toString());

                rideSummaryActivity.putExtra("distance",ride.getDistance());
                rideSummaryActivity.putExtra("avgSpeed",ride.getAvgSpeed());
                rideSummaryActivity.putExtra("duration",ride.getDuration());
                rideSummaryActivity.putExtra("maxSpeed",ride.getMaxSpeed());

                startActivity(rideSummaryActivity);
            }
        });
    }

    @Override
    public void handleParseRequestError(Exception ex)
    {
        progressBar.setVisibility(View.GONE);

        Intent errorIntent = new Intent(this,ErrorActivity.class);
        startActivity(errorIntent);
    }

    // should never be called...
    @Override
    public void handleParseRequestSuccess()
    {
        progressBar.setVisibility(View.GONE);
    }
}
