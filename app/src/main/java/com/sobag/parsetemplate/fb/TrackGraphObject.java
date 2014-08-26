package com.sobag.parsetemplate.fb;

import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphObject;

/**
 * Created by tzhmufl2 on 14.08.14.
 */
public interface TrackGraphObject extends OpenGraphObject
{
    // title...
    public String getTitle();
    public void setTitle(String title);

    // A URL
    public String getUrl();
    public void setUrl(String url);

    // An ID
    public String getId();
    public void setId(String id);
}
