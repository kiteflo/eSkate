package com.sobag.parsetemplate.domain;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import org.json.JSONArray;

import java.util.ArrayList;
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

    private String title;
    private User user;
    private Waypoint startPoint;
    private Waypoint endPoint;
    private JSONArray waypoints;

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
}
