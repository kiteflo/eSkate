package com.sobag.parsetemplate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.inject.Inject;
import com.sobag.parsetemplate.domain.RideHolder;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.BitmapUtility;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.GeoUtility;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class LocationActivity extends CommonActivity
    implements LocationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Inject
    RideHolder rideHolder;

    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @Inject
    FontUtility fontUtility;

    @InjectView(tag = "tv_location")
    TextView tvLocation;
    @InjectView(tag = "tv_label")
    TextView tvLabel;
    @InjectView(tag = "ll_slider")
    LinearLayout ll_slider;


    @InjectView(tag = "iv_reference")
    ImageView iv_reference;
    @InjectView(tag = "rl_reference_container")
    RelativeLayout rl_reference_container;

    private GoogleMap map = null;
    private LocationManager locationManager = null;
    private Marker marker = null;
    // geo statics...
    public static final int GPS_UPDATE_INTERVAL = 10000;
    public static final int SATTELITE_UPDATE_INTERVAL = 10000;
    public static final double DISTANCE_FILER = 80;

    private List<LatLng> wayPoints = new ArrayList<LatLng>();
    private Polyline polyLine;

    // statics
    public static int CAPTURE_IMAGE_RESULT = 49; // why 47? just like this number...
    private String mCurrentPhotoPath;

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
        fontUtility.applyFontToComponent(tvLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag_map))
                .getMap();

        checkLocationServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAPTURE_IMAGE_RESULT && resultCode == RESULT_OK)
        {
            File image = null;
            try
            {
                image = new File(new URI(mCurrentPhotoPath));
            }
            catch (URISyntaxException ex)
            {
                ex.printStackTrace();
            }

            if(image.exists())
            {
                Bitmap myBitmap = null;
                int boxWidth = iv_reference.getWidth();
                int boxHeight = iv_reference.getHeight();

                // decode bitmap using our super helper....
                try
                {
                     myBitmap = new BitmapUtility(getApplicationContext()).getDownsampledBitmap(Uri.parse(mCurrentPhotoPath),
                            boxWidth, boxHeight);
                }
                catch (IOException ex)
                {
                    Ln.e(ex);
                }

                RelativeLayout rlNew = new RelativeLayout(this);
                rlNew.setLayoutParams(rl_reference_container.getLayoutParams());

                ImageView ivNew = new ImageView(this);
                ivNew.setLayoutParams(iv_reference.getLayoutParams());

                ivNew.setImageBitmap(myBitmap);
                ivNew.getLayoutParams().width = iv_reference.getWidth();
                ivNew.getLayoutParams().height = iv_reference.getHeight();
                ivNew.setScaleType(ImageView.ScaleType.FIT_XY);

                RelativeLayout.LayoutParams reference_params = (RelativeLayout.LayoutParams) iv_reference.getLayoutParams();
                reference_params.setMargins(10, 10, 10, 10);
                iv_reference.setLayoutParams(reference_params);

                rlNew.addView(ivNew);
                ll_slider.addView(rlNew, 0);

                iv_reference = ivNew;
                rl_reference_container = rlNew;
            }
        }
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void onTakeImage(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File image = null;
            try
            {
                image = createImageFile();
            }
            catch (IOException ex)
            {
                // Error occurred while creating the File
                Ln.e(ex);
            }
            // Continue only if the File was successfully created
            if (image != null)
            {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(image));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_RESULT);
            }
        }
    }

    public void onStartRiding(View view)
    {
        Intent ridingActivityIntent = new Intent(this,RidingActivity.class);
        startActivity(ridingActivityIntent);

        finish();
    }

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

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,SATTELITE_UPDATE_INTERVAL,0,this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,0,this);

        // show progressbar...
        progressBar.setVisibility(View.VISIBLE);
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + img.getAbsolutePath();

        // Save a file: path for use with ACTION_VIEW intents
        rideHolder.getRideImages().add("file:" + img.getAbsolutePath());

        return img;
    }

    // ------------------------------------------------------------------------
    // geo handling
    // ------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location)
    {
        // hide progressbar....
        progressBar.setVisibility(View.GONE);

        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        marker = map.addMarker(new MarkerOptions().position(position).title("YOU"));

        // move camera...
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
