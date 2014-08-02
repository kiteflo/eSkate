package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by tzhmufl2 on 08.07.14.
 */
@ParseClassName("Waypoint")
public class Waypoint extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private ParseGeoPoint waypoint;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    // required by Parse SDK
    public Waypoint(){}

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public ParseGeoPoint getWaypoint()
    {
        return getParseGeoPoint("waypoint");
    }

    public void setWaypoint(ParseGeoPoint waypoint)
    {
        put("waypoint",waypoint);
    }
}
