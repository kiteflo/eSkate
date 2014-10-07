package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.sobag.parsetemplate.enums.Discipline;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tzhmufl2 on 08.07.14.
 */
@ParseClassName("Ride")
public class Ride extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Discipline discipline;

    private String title;
    private User user;
    private Waypoint startPoint;
    private Waypoint endPoint;
    private JSONArray waypoints;
    private ParseFile mapImage;
    private ParseRelation<RideImage> rideImages;
    private Date rideDate;
    private Date startTime;
    private Date endTime;
    private double distance;
    private double maxSpeed;
    private double avgSpeed;
    private ParseRelation<Weapon> board;
    private String duration;
    private String city;
    private String country;
    private String countryCode;

    // helper properties...
    private List<RideImage> images = new ArrayList<RideImage>();

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    // required by Parse SDK
    public Ride(){}

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title",title);
    }

    public User getUser()
    {
        return (User)get("user");
    }

    public void setUser(User user)
    {
        put("user",user);
    }

    public Waypoint getStartPoint()
    {
        return (Waypoint)get("startPoint");
    }

    public void setStartPoint(Waypoint startPoint)
    {
        put("startPoint",startPoint);
    }

    public Waypoint getEndPoint()
    {
        return (Waypoint)get("endPoint");
    }

    public void setEndPoint(Waypoint endPoint)
    {
        put("endPoint",endPoint);
    }

    public JSONArray getWaypoints()
    {
        return getJSONArray("waypoints");
    }

    public void setWaypoints(JSONArray waypoints)
    {
        put("waypoints",waypoints);
    }

    // images

    // map image

    public ParseFile getMapImage() {
        return getParseFile("mapImage");
    }

    public void setMapImage(ParseFile image) {
        put("mapImage",image);
    }

    // ride images

    public List<RideImage> getImages()
    {
        return images;
    }

    public void setImages(List<RideImage> images)
    {
        this.images = images;
    }

    public ParseRelation<RideImage> getRideImages()
    {
        return getRelation("rideImages");
    }

    public Date getEndTime()
    {
        return getDate("endTime");
    }

    public void setEndTime(Date endTime)
    {
        put("endTime",endTime);
    }

    public Date getStartTime()
    {
        return getDate("startTime");
    }

    public void setStartTime(Date startTime)
    {
        put("startTime",startTime);
    }

    public double getDistance()
    {
        return getDouble("distance");
    }

    public void setDistance(double distance)
    {
        put("distance",distance);
    }

    public double getMaxSpeed()
    {
        return getDouble("maxSpeed");
    }

    public void setMaxSpeed(double maxSpeed)
    {
        put("maxSpeed",maxSpeed);
    }

    public double getAvgSpeed()
    {
        return getDouble("avgSpeed");
    }

    public void setAvgSpeed(double avgSpeed)
    {
        put("avgSpeed",avgSpeed);
    }

    public Date getRideDate()
    {
        return getDate("rideDate");
    }

    public void setRideDate(Date rideDate)
    {
        put("rideDate",rideDate);
    }

    public ParseRelation<Weapon> getBoard()
    {
        return getRelation("board");
    }

    public String getDuration()
    {
        return getString("duration");
    }

    public void setDuration(String duration)
    {
        put("duration",duration);
    }

    public String getCity()
    {
        return getString("city");
    }

    public void setCity(String city)
    {
        put("city",city);
    }

    public String getCountry()
    {
        return getString("country");
    }

    public void setCountry(String country)
    {
        put("country",country);
    }

    public Discipline getDiscipline()
    {
        return Discipline.valueOf(getString("discipline"));
    }

    public void setDiscipline(Discipline discipline)
    {
        put("discipline",discipline.toString());
    }

    public String getCountryCode()
    {
        return getString("countryCode");
    }

    public void setCountryCode(String countryCode)
    {
        put("countryCode",countryCode);
    }
}
