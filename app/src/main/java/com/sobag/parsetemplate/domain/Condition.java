package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Condition")
public class Condition extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String key;
    private String description;
    private String minValue;
    private String maxValue;

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getKey()
    {
        return getString("key");
    }

    public void setKey(String key)
    {
        put("key",key);
    }

    public String getMinValue()
    {
        return getString("minValue");
    }

    public void setMinValue(String minValue)
    {
        put("minValue",minValue);
    }

    public String getMaxValue()
    {
        return getString("maxValue");
    }

    public void setMaxValue(String maxValue)
    {
        put("maxValue",maxValue);
    }

    public String getDescription()
    {
        return getString("description");
    }

    public void setDescription(String description)
    {
        put("description",description);
    }
}
