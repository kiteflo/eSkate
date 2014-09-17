package com.sobag.parsetemplate.domain;

import android.graphics.Bitmap;

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

    // non parse members...
    private Bitmap thumbnail;
    private byte[] rawData;

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

    public void resetRideImage()
    {
        rideImage = null;
    }

    public Bitmap getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public byte[] getRawData()
    {
        return rawData;
    }

    public void setRawData(byte[] rawData)
    {
        this.rawData = rawData;
    }
}
