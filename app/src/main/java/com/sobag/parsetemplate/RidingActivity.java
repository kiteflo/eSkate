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
import android.view.View;
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

    @InjectView(tag = "tv_distance")
    TextView tvDistance;
    @InjectView(tag = "tv_avgSpeed")
    TextView tvAvgSpeed;

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

    private GoogleMap map = null;
    private LocationManager locationManager = null;
    private Marker marker = null;

    // geo statics...
    public static final int GPS_UPDATE_INTERVAL = 0;
    public static final int SATTELITE_UPDATE_INTERVAL = 0;
    public static double DISTANCE_FILTER = 20;
    public static double ACCURACY_MINIMUM = 0.5;
    private Location previousLocation = null;

    // ride
    private RideHolder rideHolder = new RideHolder();

    // ride tracking...
    private float currentSpeed = 0;

    public static int CAPTURE_IMAGE_RESULT = 51; // why 49? just like this number...

    // flag to decide whether finish or save should be triggered...
    private boolean save = false;

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
        fontUtility.applyFontToComponent(tvFinish,R.string.default_font,
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

            // set end point
            rideHolder.setEndPosition(new LatLng(previousLocation.getLatitude(),
                    previousLocation.getLongitude()));

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

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

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
        rideHolder.getRideImages().add("file:" + img.getAbsolutePath());

        return img;
    }

    // ------------------------------------------------------------------------
    // geo handling
    // ------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location)
    {
        float accuracy = location.getAccuracy();

        if (location.getProvider() == LocationManager.GPS_PROVIDER)
        {
            applyGPSBasedSettings();
        }

        // hide progressbar....
        progressBar.setVisibility(View.GONE);

        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        // first point?
        if (marker == null)
        {
            marker = map.addMarker(new MarkerOptions().position(position).title("YOU"));

            // move camera...
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

            rideHolder.getWaypoints().add(position);

            // set start position...
            rideHolder.setStartPosition(position);

            previousLocation = location;
        }

        else if (location.distanceTo(previousLocation) > DISTANCE_FILTER && accuracy > ACCURACY_MINIMUM)
        {
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if (provider == LocationManager.GPS_PROVIDER)
        {
            applyGPSBasedSettings();
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if (provider == LocationManager.GPS_PROVIDER)
        {
            applyNetworkCarrierSettings();
        }
        else if (provider == LocationManager.NETWORK_PROVIDER)
        {
            applyGPSBasedSettings();
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    /**
     * Check whether way points need to be added...in case way point is more than
     * 50 meters away from previous way point we add.
     * @param point
     * @param wayPoints
     * @return
     */
    private boolean checkWhetherWayPointNeedsToBeAdded(LatLng point,List<LatLng> wayPoints)
    {
        LatLng lastPoint = null;

        try
        {
            lastPoint = wayPoints.get(wayPoints.size() - 1);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            return true;
        }

        double distance = GeoUtility.distanceBetween(point.latitude, point.longitude,
                lastPoint.latitude, lastPoint.longitude);

        if (distance > DISTANCE_FILTER)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void applyGPSBasedSettings()
    {
        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,0,this);

        // update distance filter to higher accuracy...
        DISTANCE_FILTER = 10;
    }

    private void applyNetworkCarrierSettings()
    {
        // remove updates in order to add GPS only updates as GPS is available
        // now...
        locationManager.removeUpdates(this);

        // add gps updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_UPDATE_INTERVAL,0,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,GPS_UPDATE_INTERVAL,0,this);

        // update distance filter to higher accuracy...
        DISTANCE_FILTER = 50;
    }

    private void updateRidingResults(Location location)
    {
        currentSpeed = location.getSpeed()/1000;
        rideHolder.getSpeedMeasurePoints().add(currentSpeed);

        if (currentSpeed > rideHolder.getMaxSpeed())
        {
            rideHolder.setMaxSpeed(currentSpeed);
        }

        // calculate avg speed...
        float total = 0;
        for (float value : rideHolder.getSpeedMeasurePoints())
        {
            total = total + value;
        }
        rideHolder.setAvgSpeed(total / rideHolder.getSpeedMeasurePoints().size());

        // calculate distance...
        rideHolder.setDistance(rideHolder.getDistance() + (location.distanceTo(previousLocation)));

        // set UI values...
        tvMaxSpeed.setText(String.format("%.2f", 22.6767867));
        tvAvgSpeed.setText(String.format("%.2f", rideHolder.getAvgSpeed()));
        tvDistance.setText(String.format("%.2f", rideHolder.getDistance()/1000));
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
        }
    }

}
