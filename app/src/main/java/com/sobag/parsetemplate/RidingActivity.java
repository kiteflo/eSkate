package com.sobag.parsetemplate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideHolder;
import com.sobag.parsetemplate.domain.Waypoint;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.services.ParseInitializationService;
import com.sobag.parsetemplate.services.ParseRequestService;
import com.sobag.parsetemplate.services.RequestListener;
import com.sobag.parsetemplate.util.BitmapUtility;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.GeoUtility;
import com.sobag.parsetemplate.util.TimerUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class RidingActivity extends CommonActivity
        implements LocationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    // service injection...
    @Inject
    ParseRequestService parseRequestService;

    @Nullable
    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @Inject
    FontUtility fontUtility;

    @InjectView(tag = "tv_location")
    TextView tvLocation;
    @InjectView(tag = "tv_finish")
    TextView tvFinish;
    @InjectView(tag = "tv_pause")
    TextView tvPause;
    @InjectView(tag = "but_pause")
    RelativeLayout butPause;
    @InjectView(tag = "view_divider_footer")
    View viewDividerFooter;

    @InjectView(tag = "tv_distance")
    TextView tvDistance;
    @InjectView(tag = "tv_avgSpeed")
    TextView tvAvgSpeed;
    @InjectView(tag = "tv_timer")
    TextView tvTimer;

    @InjectView(tag = "tv_maxSpeed")
    TextView tvMaxSpeed;

    @InjectView(tag = "ll_shareContainer")
    LinearLayout llShareContainer;

    @InjectView(tag = "ll_container")
    LinearLayout llContainer;

    @InjectView(tag = "sv_scroll")
    ScrollView svScroll;

    @InjectView(tag = "tv_facebook")
    TextView tvFacebook;

    @InjectView(tag = "tv_eskate")
    TextView tvEskate;

    @InjectView(tag = "tv_describe")
    TextView tvDescribe;

    @InjectView(tag = "et_rideTitle")
    EditText etRideTitle;

    @InjectView(tag = "custom_button_protected")
    RelativeLayout custom_button_protected;
    @InjectView(tag = "custom_button")
    RelativeLayout custom_button;
    @InjectView(tag = "start_button")
    RelativeLayout start_button;
    @InjectView(tag = "view_lock_protected")
    View viewLockProtected;


    private GoogleMap map = null;
    private LocationManager locationManager = null;
    private Marker marker = null;

    // geo statics...
    public static final int GPS_UPDATE_INTERVAL = 0;
    public static final int SATTELITE_UPDATE_INTERVAL = 0;
    public static double DISTANCE_FILTER_GPS = 8;
    public static double DISTANCE_FILTER_NETWORK = 15;
    public static double DISTANCE_MAX_FILTER_NETWORK = 60;
    private double currentDistanceFilter;
    public static double ACCURACY_MINIMUM_GPS = 5;
    public static double ACCURACY_MINIMUM_NETWORK = 30;
    private double currentAccuracyFilter;
    private Location previousLocation = null;

    // ride
    @Inject
    private RideHolder rideHolder;

    // timer
    private TimerUtility timerUtility;

    private boolean paused = true;

    // ride tracking...
    private double currentSpeed = 0;

    public static int CAPTURE_IMAGE_RESULT = 51; // why 49? just like this number...

    // flag to decide whether finish or save should be triggered...
    private boolean save = false;

    // animation stuff
    boolean animationFinished = false;

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding);

        // apply fonts
        fontUtility.applyFontToComponent(tvLocation,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvFacebook,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvEskate,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvDescribe,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(etRideTitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag_map))
                .getMap();

        // init map & location manager...
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initMapToCurrentPosition();

        custom_button_protected.setOnTouchListener(new View.OnTouchListener()
        {
            Animation animFadein = null;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    animationFinished = true;

                    viewLockProtected.setVisibility(View.VISIBLE);

                    // load the animation
                    animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.button_lock_animation);
                    animFadein.setAnimationListener(new ButtonAnimationListener());

                    // start the animation
                    viewLockProtected.startAnimation(animFadein);
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (animFadein.hasEnded())
                    {
                        custom_button_protected.setVisibility(View.GONE);
                        custom_button.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        animationFinished = false;

                        viewLockProtected.setVisibility(View.INVISIBLE);

                        animFadein.cancel();
                        viewLockProtected.clearAnimation();
                    }
                }

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAPTURE_IMAGE_RESULT && resultCode == RESULT_OK)
        {
            File image = null;
            try
            {
                image = new File(new URI(rideHolder.getRideImages().get(rideHolder.getRideImages().size()-1)));
            }
            catch (URISyntaxException ex)
            {
                ex.printStackTrace();
            }

            if(image != null && image.exists())
            {
                Bitmap myBitmap = null;
                int boxWidth = 120;
                int boxHeight = 120;

                // decode bitmap using our super helper....
                try
                {
                    myBitmap = new BitmapUtility(getApplicationContext()).getDownsampledBitmap(Uri.parse(
                                    rideHolder.getRideImages().get(rideHolder.getRideImages().size()-1)
                            ),
                            boxWidth, boxHeight);

                    // squared image? id so we crop down...
                    myBitmap = BitmapUtility.createSquaredBitmap(myBitmap);
                }
                catch (IOException ex)
                {
                    Ln.e(ex);
                }

                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(120, 135, conf);
                Canvas canvas1 = new Canvas(bmp);

                // draw photo...
                canvas1.drawBitmap(myBitmap, null, new Rect(0,0,120,120), null);
                myBitmap.recycle();

                // paint marker...
                Paint background = new Paint();
                Bitmap marker = BitmapFactory.decodeResource(getResources(),
                        R.drawable.marker);
                marker = Bitmap.createScaledBitmap(marker, 120, 120, false);
                canvas1.drawBitmap(marker, 0,0, background);
                marker.recycle();

                // footer
                Bitmap markerFooter = BitmapFactory.decodeResource(getResources(),
                        R.drawable.marker_bottom);
                markerFooter = Bitmap.createScaledBitmap(markerFooter, 120, 15, false);
                canvas1.drawBitmap(markerFooter, 0,120, background);
                markerFooter.recycle();


                //add marker to Map
                map.addMarker(new MarkerOptions().position(new LatLng(previousLocation.getLatitude(),
                        previousLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 1));
            }
        }
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void onStart(View view)
    {
        // init rideHolder
        rideHolder.setStartTime(new Date());

        // enable tracking
        paused = false;

        if (previousLocation != null)
        {
            rideHolder.setStartPosition(new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()));
        }

        // start timer
        timerUtility = new TimerUtility();
        timerUtility.startTimer(tvTimer);

        start_button.setVisibility(View.GONE);
        custom_button_protected.setVisibility(View.VISIBLE);
    }

    public void onTakePhoto(View view)
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

    public void onFinish(View view)
    {
        // finish ride...
        if (!save)
        {
            // stop GPS updates...
            locationManager.removeUpdates(this);

            // stop timer
            timerUtility.stopTimer();

            // set end point
            rideHolder.setEndPosition(new LatLng(previousLocation.getLatitude(),
                    previousLocation.getLongitude()));

            // set end time
            rideHolder.setEndTime(new Date());
            rideHolder.setDuration(tvTimer.getText().toString());

            // update UI (hide oause button...
            butPause.setVisibility(View.GONE);
            viewDividerFooter.setVisibility(View.GONE);

            // display publish footer...
            int height = llContainer.getHeight();
            llShareContainer.setVisibility(View.VISIBLE);

            // re-apply height
            llContainer.setMinimumHeight(height);
            llContainer.getLayoutParams().height = height;

            // scroll to bottom...
            svScroll.post(new Runnable()
            {
                public void run()
                {
                    svScroll.smoothScrollTo(0, svScroll.getBottom());
                }
            });

            // focus edittext
            etRideTitle.requestFocus();

            // create map snapshot...
            try
            {
                captureMapScreen();
            }
            catch (Exception e)
            {
                Ln.e(e);
            }

            // apply button label...
            tvFinish.setText(getString(R.string.but_save));

            save = true;
        }

        // save ride...
        else
        {
            // prepare ride...
            final Ride ride = new Ride();

            // apply title...
            rideHolder.setTitle(etRideTitle.getText().toString());

            // trigger parse save operation...
            parseRequestService.saveRide(new SaveRideRequest(),rideHolder);
        }
    }

    public void onPause(View view)
    {
        if (!paused)
        {
            Toast.makeText(this, getString(R.string.msg_paused), Toast.LENGTH_LONG).show();
            paused = true;
            tvPause.setText(getString(R.string.but_resume));
            timerUtility.pauseTimer();
        }
        else
        {
            paused = false;
            tvPause.setText(getString(R.string.but_pause));
            custom_button.setVisibility(View.INVISIBLE);
            custom_button_protected.setVisibility(View.VISIBLE);
            viewLockProtected.setVisibility(View.INVISIBLE);
            timerUtility.startTimer(tvTimer);
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    private void initMapToCurrentPosition()
    {
        // apply NETWORK settings...
        applyNetworkCarrierSettings();

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();

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

        float accuracy = location.getAccuracy();
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        if (previousLocation != null)
        {
            Ln.i("Provider: " + location.getProvider() + " | accuracy: " + accuracy + " | distance to previous: " + location.distanceTo(previousLocation));

            if (previousLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER) &&
                    location.getProvider().equals(LocationManager.GPS_PROVIDER))
            {
                applyGPSBasedSettings();
                previousLocation = location;

                // ride did not start, update marker...
                if (rideHolder.getStartTime() == null)
                {
                    applyMarkerAsStartPoint(position);
                }
            }
            else if (previousLocation.getProvider().equals(LocationManager.GPS_PROVIDER) &&
                    location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
            {
                applyNetworkCarrierSettings();

                if (location.distanceTo(previousLocation) > currentDistanceFilter && accuracy > currentAccuracyFilter
                        && location.distanceTo(previousLocation) < DISTANCE_MAX_FILTER_NETWORK)
                {
                    previousLocation = location;
                }
            }
        }

        // virgin point
        else
        {
            applyMarkerAsStartPoint(position);

            previousLocation = location;
        }


        if (location.distanceTo(previousLocation) > currentDistanceFilter && accuracy > currentAccuracyFilter
                && !paused && rideHolder.getStartTime() != null)
        {
            // check for max distance...
            if (previousLocation.getProvider().length() != location.getProvider().length())
            {
                if (location.distanceTo(previousLocation) < DISTANCE_MAX_FILTER_NETWORK)
                {
                    Ln.i("Adding waypoint to route...");

                    // add waypoint to rideHolder
                    rideHolder.getWaypoints().add(position);

                    map.addPolyline(new PolylineOptions()
                            .addAll(rideHolder.getWaypoints())
                            .width(10)
                            .color(Color.RED));

                    // move camera...
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

                    // apply ride settings
                    updateRidingResults(location);

                    previousLocation = location;
                }
            }
            else
            {
                Ln.i("Adding waypoint to route...");

                // add waypoint to rideHolder
                rideHolder.getWaypoints().add(position);

                map.addPolyline(new PolylineOptions()
                        .addAll(rideHolder.getWaypoints())
                        .width(10)
                        .color(Color.RED));

                // move camera...
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

                // apply ride settings
                updateRidingResults(location);

                previousLocation = location;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Ln.i("Enabled provider: " +provider + " - switching to GPS");

        if (provider.equals(LocationManager.GPS_PROVIDER))
        {
            applyGPSBasedSettings();
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if (provider.equals(LocationManager.GPS_PROVIDER))
        {
            Ln.i("Disabled provider: " +provider + " - switching to Network");
            applyNetworkCarrierSettings();
        }
        else if (provider.equals(LocationManager.NETWORK_PROVIDER))
        {
            Ln.i("Disabled provider: " +provider + " - switching to GPS");
            applyGPSBasedSettings();
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    private void applyGPSBasedSettings()
    {
        Ln.i("Applying GPS based setting...");

        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,0,this);

        // update distance filter to higher accuracy...
        currentDistanceFilter = DISTANCE_FILTER_GPS;
        currentAccuracyFilter = ACCURACY_MINIMUM_GPS;
    }

    private void applyNetworkCarrierSettings()
    {
        Ln.i("Applying network based setting...");

        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,0,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,GPS_UPDATE_INTERVAL,0,this);

        // update distance filter to higher accuracy...
        currentDistanceFilter = DISTANCE_FILTER_NETWORK;
        currentAccuracyFilter = ACCURACY_MINIMUM_NETWORK;
    }

    private void updateRidingResults(Location location)
    {
        currentSpeed = location.getSpeed()/1000;
        rideHolder.getSpeedMeasurePoints().add(currentSpeed);

        if (currentSpeed > rideHolder.getMaxSpeed())
        {
            rideHolder.setMaxSpeed(currentSpeed);
        }

        // set avg speed...
        // s=v*t <=> v=s/t
        double avgSpeed = (rideHolder.getDistance()/1000)/timerUtility.getSeconds();
        rideHolder.setAvgSpeed(avgSpeed);

        // calculate distance...
        rideHolder.setDistance(rideHolder.getDistance() + (location.distanceTo(previousLocation)));

        // set UI values...
        tvMaxSpeed.setText(String.format("%.2f", rideHolder.getMaxSpeed()));
        tvAvgSpeed.setText(String.format("%.2f", rideHolder.getAvgSpeed()));
        tvDistance.setText(String.format("%.2f", rideHolder.getDistance()/1000));
    }

    // map helpers

    private void applyMarkerAsStartPoint(LatLng position)
    {
        if (marker != null)
        {
            marker.remove();
        }

        marker = map.addMarker(new MarkerOptions().position(position).title("YOU"));

        // move camera...
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

        // update marker
        if (rideHolder.getWaypoints().size() > 0)
        {
            rideHolder.getWaypoints().set(0,position);
        }
        // add marker
        else
        {
            rideHolder.getWaypoints().add(position);
        }

        // set start position...
        rideHolder.setStartPosition(position);
    }

    // ------------------------------------------------------------------------
    // map snapshot
    // ------------------------------------------------------------------------

    public void captureMapScreen()
            throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MAP_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        final File snapshotFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {
                bitmap = snapshot;
                try
                {
                    FileOutputStream out = new FileOutputStream(snapshotFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                    rideHolder.setMapImage(snapshotFile.getAbsolutePath());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        map.snapshot(callback);
    }

    // ------------------------------------------------------------------------
    // inner classes...
    // ------------------------------------------------------------------------

    public class SaveRideRequest implements RequestListener
    {

        @Override
        public void handleStartRequest()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        // should not be called...
        @Override
        public void handleRequestResult(List result)
        {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void handleParseRequestError(Exception ex)
        {
            progressBar.setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(),"Saved ride...",Toast.LENGTH_LONG).show();
        }

        @Override
        public void handleParseRequestSuccess()
        {
            progressBar.setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(),"Saved ride...",Toast.LENGTH_LONG).show();

            // switch to rides overview...
            Intent ridesActivityIntent = new Intent(getApplicationContext(),RidesActivity.class);
            startActivity(ridesActivityIntent);
        }
    }

    public class ButtonAnimationListener
            implements Animation.AnimationListener
    {

        @Override
        public void onAnimationStart(Animation animation)
        {

        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            if (animationFinished)
            {
                custom_button.setVisibility(View.VISIBLE);
                custom_button_protected.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation)
        {

        }
    }
}
