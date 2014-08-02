package com.sobag.parsetemplate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Group ride properties...
 */
public class RideHolder
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String title;
    private LatLng startPosition;
    private LatLng endPosition;
    private List<LatLng> waypoints = new ArrayList<LatLng>();
    private List<String> rideImages = new ArrayList<String>();
    private List<Float> speedMeasurePoints = new ArrayList<Float>();
    private double maxSpeed;
    private double avgSpeed;
    private double distance;
    private String mapImage;

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

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

    public List<Float> getSpeedMeasurePoints()
    {
        return speedMeasurePoints;
    }

    public void setSpeedMeasurePoints(List<Float> speesMeasurePoints)
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
}
