package com.sobag.parsetemplate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.ToggleButton;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.inject.Inject;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parse.ParseException;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideHolder;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.enums.GenericRequestCode;
import com.sobag.parsetemplate.fb.FacebookHandler;
import com.sobag.parsetemplate.services.ParseRequestService;
import com.sobag.parsetemplate.services.RequestListener;
import com.sobag.parsetemplate.util.BitmapUtility;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.ResourceUtility;
import com.sobag.parsetemplate.util.TimerUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class RidingActivity extends CommonCameraActivity
        implements LocationListener, Validator.ValidationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Validator validator;
    private RidingActivity selfReference;

    // service injection...
    @Inject
    ParseRequestService parseRequestService;

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
    @InjectView(tag = "toggle_facebook")
    ToggleButton toggleFacebook;
    @InjectView(tag = "toggle_eskate")
    ToggleButton toggleEskate;
    @InjectView(tag = "tv_provider")
    TextView tvProvider;
    @InjectView(tag = "iv_temp")
    ImageView ivTemp;

    @InjectView(tag = "tv_distance")
    TextView tvDistance;
    @InjectView(tag = "tv_avgSpeed")
    TextView tvAvgSpeed;
    @InjectView(tag = "tv_timer")
    TextView tvTimer;
    @InjectView(tag = "tv_speed")
    TextView tvSpeed;
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

    @Required(order = 1, message = "msg_requiredField")
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
    public static final int GPS_MIN_DISTANCE = 5;
    public static final int GPS_ACCURACY = 15;
    public static final int NETWORK_MIN_DISTANCE = 10;
    public static final int NETWORK_ACCURACY = 35;
    public static final int GPS_UPDATE_INTERVAL = 2000;
    public static final int NETWORK_UPDATE_INTERVAL = 3000;
    public static double DISTANCE_MAX_FILTER_NETWORK = 30;
    private Location previousLocation = null;
    private int currentAccuracy;
    private int currentCameraZoom = 17;

    // ride
    @Inject
    private RideHolder rideHolder;

    // timer
    private TimerUtility timerUtility;

    // riding = a ride has been started
    private boolean riding = false;
    // paused = ride paused
    private boolean paused = false;
    // gps detector
    private boolean gpsSignal = false;

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
        selfReference = this;

        // init validator....
        validator = new Validator(this);
        validator.setValidationListener(this);

        // apply fonts
        fontUtility.applyFontToComponent(tvLocation,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvFacebook,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvEskate,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvDescribe,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);



        // init map & location manager...
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag_map))
                .getMap();
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
        // facebook permission added
        else
        {
            Session session = Session.getActiveSession();

            // **************** IMPORTANT 24h hack *******************************
            // parse/facebook session is not updated automatically after adding permissions...
            // if you wanna have the newly added permissions you have to run through the if
            // condition below, this will work...
            if (session != null)
            {
                session.onActivityResult(this, requestCode, resultCode, data);
            }

            if (session.getPermissions().contains("publish_actions"))
            {
                // trigger parse save operation...
                parseRequestService.saveRide(new SaveRideRequest(),rideHolder);

                FacebookHandler fbHandler = new FacebookHandler();
                fbHandler.shareRideOnFacebook(rideHolder);
            }
            else
            {
                Toast.makeText(this,"Insufficient permissions....",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ------------------------------------------------------------------------
    // validation implementation
    // ------------------------------------------------------------------------

    /**
     * Validation passed...trigger server call...
     */
    public void onValidationSucceeded()
    {
        Ln.d("Validated successfully!");

        // create map snapshot...finish ride is triggered via callback...
        try
        {
            captureMapScreen();
        }
        catch (Exception e)
        {
            Ln.e(e);
        }
    }

    public void onValidationFailed(View failedView, Rule<?> failedRule)
    {
        Ln.d("Validation failed!");

        // little hack - within a module we can not specify a message ID directly
        // within the annotation...so we define a string constant in the annotation
        // which finally here will be translated...
        String messageID = failedRule.getFailureMessage();
        String translatedMessage = getString(ResourceUtility.getId(messageID, R.string.class));

        if (failedView instanceof EditText)
        {
            failedView.requestFocus();
            ((EditText) failedView).setError(translatedMessage);
        }
        else
        {
            Toast.makeText(this, translatedMessage, Toast.LENGTH_SHORT).show();
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
        riding = true;

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

            // stop speeding
            tvSpeed.setText(String.format("%.2f", 0f));

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

            // apply button label...
            tvFinish.setText(getString(R.string.but_save));

            save = true;
        }

        // save ride...
        else
        {
            validator.validate();
        }
    }

    private void finishRide()
    {
        // apply title...
        rideHolder.setTitle(etRideTitle.getText().toString());

        // share via facebook?
        if (toggleFacebook.isChecked())
        {
            Session session = Session.getActiveSession();

            if (! session.getPermissions().contains("publish_actions"))
            {
                List permissions = Arrays.asList("publish_actions");
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, permissions);
                session.requestNewPublishPermissions(newPermissionsRequest);
            }
            else
            {
                FacebookHandler fbHandler = new FacebookHandler();
                fbHandler.shareRideOnFacebook(rideHolder);

                // trigger parse save operation...
                parseRequestService.saveRide(new SaveRideRequest(),rideHolder);
            }
        }
        // plain save...
        else
        {
            // trigger parse save operation...
            parseRequestService.saveRide(new SaveRideRequest(),rideHolder);
        }
    }

    public void onPause(View view)
    {
        if (!paused)
        {
            locationManager.removeUpdates(this);

            paused = true;
            tvPause.setText(getString(R.string.but_resume));
            timerUtility.pauseTimer();

            // stop speeding
            tvSpeed.setText(String.format("%.2f", 0f));

            Toast.makeText(this, getString(R.string.msg_paused), Toast.LENGTH_LONG).show();
        }
        else
        {
            paused = false;
            tvPause.setText(getString(R.string.but_pause));
            custom_button.setVisibility(View.INVISIBLE);
            custom_button_protected.setVisibility(View.VISIBLE);
            viewLockProtected.setVisibility(View.INVISIBLE);
            timerUtility.startTimer(tvTimer);

            if (gpsSignal)
            {
                applyGPSBasedSettings();
            }
            else
            {
                applyNetworkCarrierSettings();
            }
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    private void initMapToCurrentPosition()
    {
        // apply NETWORK settings...
        applyNetworkCarrierSettings();

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

        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        // GPS signal detected!
        if (!gpsSignal && location.getProvider().equals(LocationManager.GPS_PROVIDER))
        {
            applyGPSBasedSettings();
        }
        else if (gpsSignal && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
        {
            applyNetworkCarrierSettings();
        }

        // ride did not start...ride preparations
        if (!riding)
        {
            applyMarkerAsStartPoint(position);

            previousLocation = location;
        }

        // ride it baby!
        else if (!paused)
        {
            Ln.i("Received location, accuracy: " +location.getAccuracy());
            tvProvider.setText("cuac:" +currentAccuracy +" prov:" +location.getProvider() + " acc:" + location.getAccuracy() + " v:" + location.getSpeed());

            if (location.getAccuracy() <= currentAccuracy)
            {
                Ln.i("Adding waypoint to route...");

                // add waypoint to rideHolder
                rideHolder.getWaypoints().add(position);

                map.addPolyline(new PolylineOptions()
                        .addAll(rideHolder.getWaypoints())
                        .width(10)
                        .color(Color.BLUE));

                // update camera zoom
                applyCameraZoom();

                // move camera...
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentCameraZoom));

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

        gpsSignal = true;

        // set header label
        tvProvider.setText(getResources().getText(R.string.header_GPS));

        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,GPS_MIN_DISTANCE,this);

        currentAccuracy = GPS_ACCURACY;
    }

    private void applyNetworkCarrierSettings()
    {
        Ln.i("Applying network based setting...");

        gpsSignal = false;

        // set header label
        tvProvider.setText(getResources().getText(R.string.header_NETWORK));

        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,GPS_MIN_DISTANCE,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,NETWORK_UPDATE_INTERVAL,NETWORK_MIN_DISTANCE,this);

        currentAccuracy = NETWORK_ACCURACY;
    }

    private void updateRidingResults(Location location)
    {
        currentSpeed = location.getSpeed()/1000*3600;
        tvSpeed.setText(String.format("%.2f", currentSpeed));

        rideHolder.getSpeedMeasurePoints().add(currentSpeed);

        if (currentSpeed > rideHolder.getMaxSpeed())
        {
            rideHolder.setMaxSpeed(currentSpeed);
        }

        // set avg speed...
        // s=v*t <=> v=s/t (m/s)
        double avgSpeed = rideHolder.getDistance()/timerUtility.getSeconds();
        // m/s => km/h
        avgSpeed = avgSpeed/1000*3600;
        rideHolder.setAvgSpeed(avgSpeed);

        // calculate distance...
        rideHolder.setDistance(rideHolder.getDistance() + (location.distanceTo(previousLocation)));

        // set UI values...
        tvMaxSpeed.setText(String.format("%.2f", rideHolder.getMaxSpeed()));
        tvAvgSpeed.setText(String.format("%.2f", rideHolder.getAvgSpeed()));
        tvDistance.setText(String.format("%.2f", rideHolder.getDistance() / 1000));
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentCameraZoom));

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

        // set city...as this is so easy in Android! :)
        // try to get city name...
        if (rideHolder.getAddress() == null)
        {
            try
            {
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(position.latitude, position.longitude, 1);
                if (addresses.size() > 0)
                {
                    rideHolder.setAddress(addresses.get(0));
                }
            } catch (IOException ex)
            {
                rideHolder.setAddress(null);
            }
        }
    }

    /**
     * Set current camera zoom based on current ride distance...
     */
    private void applyCameraZoom()
    {
        if (rideHolder.getDistance() >= 300)
        {
            currentCameraZoom = 16;
        }
        else if (rideHolder.getDistance() >= 500)
        {
            currentCameraZoom = 15;
        }
        else if (rideHolder.getDistance() >= 1000)
        {
            currentCameraZoom = 14;
        }
        else if (rideHolder.getDistance() >= 2000)
        {
            currentCameraZoom = 13;
        }
        else if (rideHolder.getDistance() >= 4000)
        {
            currentCameraZoom = 12;
        }
        else if (rideHolder.getDistance() >= 8000)
        {
            currentCameraZoom = 11;
        }
        else if (rideHolder.getDistance() >= 30000)
        {
            currentCameraZoom = 11;
        }
        else
        {
            currentCameraZoom = 17;
        }
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
                // lets do some pixel magic!
                // ratio footer : imageheight: 1:4.1
                int imageHeight = bitmap.getHeight();
                int imageWidth = bitmap.getWidth();

                // calculate footer height...
                int footerHeight = new Double(imageHeight / 4.1).intValue();

                Bitmap bitmapWithBorder = Bitmap.createBitmap(bitmap.getWidth(), imageHeight + footerHeight, bitmap.getConfig());

                Canvas canvas = new Canvas(bitmapWithBorder);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, null);

                // footer padding ratio: 44% padding, 56% text
                int totalTextHeight = new Double(footerHeight * 0.56).intValue();

                // large text : description ration = 70% large text, 30% small
                int largeTextHeight = new Double(0.7 * totalTextHeight).intValue();
                int smallTextHeight = new Double(0.3 * totalTextHeight).intValue();

                // calculate default padding
                int padding = new Double((footerHeight * 0.44) / 2).intValue();

                // calculate default cell width (ratio 1:4.57)
                int defaulCellWidth = new Double(imageWidth / 4.57).intValue();

                // distance value
                Paint distance = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                distance.setColor(Color.BLACK);
                // text size in pixels
                distance.setTextSize(largeTextHeight);
                // text shadow
                distance.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                int x = padding;
                int y = bitmap.getHeight() + padding + largeTextHeight;
                canvas.drawText(String.format("%.2f", rideHolder.getDistance() / 1000), x, y, distance);

                // distance label
                Paint distanceLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                distanceLabel.setColor(Color.BLACK);
                // text size in pixels
                distanceLabel.setTextSize(smallTextHeight);
                // text shadow
                distanceLabel.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                x = x; // just for symmetric code reasons...
                y = bitmap.getHeight() + padding + smallTextHeight + largeTextHeight;
                canvas.drawText("distance (km)", x, y, distanceLabel);

                // seperator
                Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawLine(defaulCellWidth, bitmap.getHeight() + padding,
                        defaulCellWidth,
                        bitmap.getHeight() + padding + totalTextHeight, line);

                int timeBoxWidth = new Double(1.5 * defaulCellWidth).intValue();

                // time value
                Paint time = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                time.setColor(Color.BLACK);
                // text size in pixels
                time.setTextSize(largeTextHeight);
                // text shadow
                time.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                x = defaulCellWidth + padding;
                y = bitmap.getHeight() + padding + largeTextHeight;
                canvas.drawText(rideHolder.getDuration(), x, y, time);

                // time label
                Paint timeLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                timeLabel.setColor(Color.BLACK);
                // text size in pixels
                timeLabel.setTextSize(smallTextHeight);
                // text shadow
                timeLabel.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                x = x;
                y = y + smallTextHeight;
                canvas.drawText("duration", x, y, timeLabel);

                // seperator
                Paint line2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawLine(defaulCellWidth + timeBoxWidth, bitmap.getHeight() + padding,
                        defaulCellWidth + timeBoxWidth, y, line2);

                // maxSpeed value
                Paint maxSpeed = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                maxSpeed.setColor(Color.BLACK);
                // text size in pixels
                maxSpeed.setTextSize(largeTextHeight);
                // text shadow
                maxSpeed.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                x = defaulCellWidth + timeBoxWidth + padding;
                y = bitmap.getHeight() + padding + largeTextHeight;
                canvas.drawText(String.format("%.2f", rideHolder.getMaxSpeed()), x, y, maxSpeed);

                // time label
                Paint maxLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                maxLabel.setColor(Color.BLACK);
                // text size in pixels
                maxLabel.setTextSize(smallTextHeight);
                // text shadow
                maxLabel.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                x = x;
                y = y + smallTextHeight;
                canvas.drawText("max speed (km)", x, y, timeLabel);

                // apply board...
                try
                {
                    int yPos = imageHeight + footerHeight/2;
                    int xPos = imageWidth -padding -50;

                    Bitmap board = BitmapFactory.decodeByteArray(rideHolder.getBoard().getImage().getData(), 0,
                            rideHolder.getBoard().getImage().getData().length);

                    // keep aspect ration
                    board = BitmapUtility.createSquaredBitmap(board);
                    board = Bitmap.createScaledBitmap(board, 100, 100, false);
                    board = BitmapUtility.cropImageToCircle(board);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    canvas.drawBitmap(board,xPos-50,yPos-50,paint);

                    Paint border = new Paint();
                    border.setXfermode(null);
                    border.setStyle(Paint.Style.STROKE);
                    border.setColor(getResources().getColor(R.color.poisonGreen));
                    border.setStrokeWidth(2);
                    canvas.drawCircle(xPos, yPos, 50, border);
                } catch (ParseException ex)
                {
                    ex.printStackTrace();
                }

                // apply banner
                if (rideHolder.getAddress() != null)
                {
                    String city = rideHolder.getAddress().getLocality();
                    String country = rideHolder.getAddress().getCountryName();
                    String text = city + " (" +country +")";

                    int yPos = padding;
                    int xPos = 2 * padding;
                    int bannerWidth = imageWidth - 4 * padding;
                    int bannerHeight = footerHeight+footerHeight*1/5;
                    Bitmap banner = BitmapFactory.decodeResource(getResources(), R.drawable.banner_blue);
                    banner = Bitmap.createScaledBitmap(banner, bannerWidth, bannerHeight, false);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    canvas.drawBitmap(banner, xPos, yPos, paint);
                    // apply text to banner...
                    Paint titleLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
                    // text color - #3D3D3D
                    titleLabel.setColor(Color.WHITE);
                    // text size in pixels
                    titleLabel.setTextSize(largeTextHeight);
                    Rect bounds = new Rect();
                    titleLabel.getTextBounds(text, 0, text.length(), bounds);

                    // subtitle
                    String subtitle = etRideTitle.getText().toString();
                    // apply text to banner...
                    Paint subtitleLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
                    // text color - #3D3D3D
                    subtitleLabel.setColor(Color.WHITE);
                    // text size in pixels
                    subtitleLabel.setTextSize(smallTextHeight);
                    Rect subbounds = new Rect();
                    subtitleLabel.getTextBounds(subtitle, 0, subtitle.length(), subbounds);

                    // Draw title
                    x = xPos + new Float((bannerWidth - bounds.width()) / 2).intValue();
                    y = yPos + new Float((bannerHeight - bounds.height() - subbounds.height() - padding/2) / 2).intValue() +bounds.height();
                    canvas.drawText(text, x, y, titleLabel);

                    x = xPos + new Float((bannerWidth - subbounds.width()) / 2).intValue();
                    y = y + subbounds.height() +padding/2;
                    canvas.drawText(subtitle, x, y, subtitleLabel);


                }

                try
                {
                    FileOutputStream out = new FileOutputStream(snapshotFile);
                    bitmapWithBorder.compress(Bitmap.CompressFormat.PNG, 100, out);

                    // TODO: not really required to write the file to disk...maybe interesting for
                    // image caching...
                    rideHolder.setMapImage(snapshotFile.getAbsolutePath());
                    rideHolder.setMapImageBitmap(bitmapWithBorder);

                    // TODO: remove: visual checkpoint...
                    ivTemp.setImageBitmap(bitmapWithBorder);

                    finishRide();
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
        public void handleGenericRequestResult(GenericRequestCode code, Object result)
        {

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
