package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by tzhmufl2 on 08.07.14.
 */
@ParseClassName("Board")
public class Board extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String title;
    private String subTitle;
    private ParseFile image;

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title",title);
    }

    public String getSubTitle() {
        return getString("subTitle");
    }

    public void setSubTitle(String subTitle) {
        put("subTitle",subTitle);
    }

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public void setImage(ParseFile image) {
        put("image",image);
    }
}
