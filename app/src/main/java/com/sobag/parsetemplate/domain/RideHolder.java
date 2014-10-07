package com.sobag.parsetemplate.domain;

import android.graphics.Bitmap;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Group ride properties...
 */
@Singleton
public class RideHolder
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Date startTime;
    private Date endTime;
    private String title;
    private LatLng startPosition;
    private LatLng endPosition;
    private List<LatLng> waypoints = new ArrayList<LatLng>();
    private List<String> rideImages = new ArrayList<String>();
    private List<Double> speedMeasurePoints = new ArrayList<Double>();
    private double maxSpeed;
    private double avgSpeed;
    private double distance;
    private double speed;
    private String mapImage;
    private Bitmap mapImageBitmap;
    private Weapon weapon;
    private String duration;
    private Address address;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------


    public double getSpeed()
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public LatLng getStartPosition()
    {
        return startPosition;
    }

    public void setStartPosition(LatLng startPosition)
    {
        this.startPosition = startPosition;
    }

    public LatLng getEndPosition()
    {
        return endPosition;
    }

    public void setEndPosition(LatLng endPosition)
    {
        this.endPosition = endPosition;
    }

    public List<LatLng> getWaypoints()
    {
        return waypoints;
    }

    public void setWaypoints(List<LatLng> waypoints)
    {
        this.waypoints = waypoints;
    }

    public List<String> getRideImages()
    {
        return rideImages;
    }

    public void setRideImages(List<String> rideImages)
    {
        this.rideImages = rideImages;
    }

    public double getMaxSpeed()
    {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed()
    {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed)
    {
        this.avgSpeed = avgSpeed;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public String getMapImage()
    {
        return mapImage;
    }

    public void setMapImage(String mapImage)
    {
        this.mapImage = mapImage;
    }

    public List<Double> getSpeedMeasurePoints()
    {
        return speedMeasurePoints;
    }

    public void setSpeedMeasurePoints(List<Double> speesMeasurePoints)
    {
        this.speedMeasurePoints = speesMeasurePoints;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Weapon getWeapon()
    {
        return weapon;
    }

    public void setWeapon(Weapon weapon)
    {
        this.weapon = weapon;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public Bitmap getMapImageBitmap()
    {
        return mapImageBitmap;
    }

    public void setMapImageBitmap(Bitmap mapImageBitmap)
    {
        this.mapImageBitmap = mapImageBitmap;
    }
}
