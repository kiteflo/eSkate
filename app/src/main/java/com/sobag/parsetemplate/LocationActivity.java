package com.sobag.parsetemplate;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import javax.annotation.Nullable;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class LocationActivity extends CommonActivity
    implements LocationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Nullable
    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @Inject
    FontUtility fontUtility;

    @InjectView(tag = "tv_location")
    TextView tvLocation;
    @InjectView(tag = "tv_start")
    TextView tvStart;

    private GoogleMap map = null;
    private LocationManager locationManager = null;
    private Marker marker = null;

    // ------------------------------------------------------------------------
    // default stuff...
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // apply fonts
        fontUtility.applyFontToComponent(tvLocation,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvStart,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag_map))
                .getMap();

        checkLocationServices();
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    /**
     * Make sure location services are enabled...
     */
    private void checkLocationServices()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled)
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else
        {
            initMapToCurrentPosition();
        }
    }

    private void initMapToCurrentPosition()
    {
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0,this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,this);

        // show progressbar...
        progressBar.setVisibility(View.VISIBLE);
    }

    // ------------------------------------------------------------------------
    // geo handling
    // ------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location)
    {
        if (location.getProvider() == LocationManager.GPS_PROVIDER)
        {
            // remove updates in order to add GPS only updates as GPS is available
            // now...
            locationManager.removeUpdates(this);

            // add gps updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,this);
        }

        // hide progressbar....
        progressBar.setVisibility(View.GONE);

        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        if (marker == null)
        {
            marker = map.addMarker(new MarkerOptions().position(position).title("YOU"));
        }
        else
        {
            marker.setPosition(position);
        }

        // move camera...
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if (provider == LocationManager.GPS_PROVIDER)
        {
            // remove updates in order to add GPS only updates as GPS is available
            // now...
            locationManager.removeUpdates(this);

            // add gps updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,this);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if (provider == LocationManager.GPS_PROVIDER)
        {
            // remove updates in order to add GPS only updates as GPS is available
            // now...
            locationManager.removeUpdates(this);

            // add gps updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0,this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,this);
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

}
