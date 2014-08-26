package com.sobag.parsetemplate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sobag.parsetemplate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class RideSummaryActivity extends CommonActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private GoogleMap map = null;

    @InjectView(tag = "tv_title")
    TextView tvTitle;
    @InjectView(tag = "tv_date")
    TextView tvDate;

    @InjectView(tag = "tv_distance")
    TextView tvDistance;
    @InjectView(tag = "tv_avgSpeed")
    TextView tvAvgSpeed;
    @InjectView(tag = "tv_timer")
    TextView tvTimer;
    @InjectView(tag = "tv_maxSpeed")
    TextView tvMaxSpeed;

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_summary);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag_map))
                .getMap();

        // extract ride data
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String waypoints = intent.getStringExtra("waypoints");

        Double distance = intent.getDoubleExtra("distance",0);
        tvDistance.setText(String.format("%.2f", distance));
        Double avgSpeed = intent.getDoubleExtra("avgSpeed",0);
        tvAvgSpeed.setText(String.format("%.2f", avgSpeed));
        String duration = intent.getStringExtra("duration");
        tvTimer.setText(duration);
        Double maxSpeed = intent.getDoubleExtra("maxSpeed",0);
        tvMaxSpeed.setText(String.format("%.2f", maxSpeed));

        JSONArray points = null;
        try
        {
            points = new JSONArray(waypoints);
        }
        catch (JSONException ex)
        {
            Ln.e(ex);
        }

        // iterate...
        if (points != null)
        {
            List<LatLng> latLngPoints = new ArrayList<LatLng>();
            for (int i=0; i<points.length(); i++)
            {
                try
                {
                    JSONObject point = points.getJSONObject(i);
                    double latitude = point.getDouble("latitude");
                    double longitude = point.getDouble("longitude");

                    latLngPoints.add(new LatLng(latitude,longitude));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

            map.addPolyline(new PolylineOptions()
                    .addAll(latLngPoints)
                    .width(10)
                    .color(Color.RED));

            //add start marker to Map
            map.addMarker(new MarkerOptions().position(new LatLng(latLngPoints.get(0).latitude,latLngPoints.get(0).longitude)).title("YOU"));

            // move camera...
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPoints.get(0), 17));

        }

        // apply values to UI
        tvTitle.setText(title);
        tvDate.setText(date);
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

}
