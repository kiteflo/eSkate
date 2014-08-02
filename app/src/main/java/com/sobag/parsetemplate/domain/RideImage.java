package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by tzhmufl2 on 08.07.14.
 */
@ParseClassName("RideImage")
public class RideImage extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private ParseFile rideImage;
    private ParseGeoPoint geo;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    // required by Parse SDK
    public RideImage(){}

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public ParseGeoPoint getGeo()
    {
        return getParseGeoPoint("geo");
    }

    public void setGeo(ParseGeoPoint geo)
    {
        put("geo",geo);
    }

    public ParseFile getRideImage() {
        return getParseFile("rideImage");
    }

    public void setRideImage(ParseFile rideImage) {
        put("rideImage",rideImage);
    }
}
