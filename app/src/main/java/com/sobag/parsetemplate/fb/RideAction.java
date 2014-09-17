package com.sobag.parsetemplate.fb;

import com.facebook.model.OpenGraphAction;

/**
 * Created by tzhmufl2 on 14.08.14.
 */
public interface RideAction extends OpenGraphAction
{
    // The meal object
    public TrackGraphObject getTrack();
    public void setTrack(TrackGraphObject track);
}
