package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.sobag.parsetemplate.enums.Discipline;

/**
 * Created by tzhmufl2 on 08.07.14.
 */
@ParseClassName("Weapon")
public class Weapon extends ParseObject
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String title;
    private String subTitle;
    private ParseFile image;
    private Discipline discipline;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    // required by Parse SDK
    public Weapon(){}

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

    public void resetImage() {image = null;}

    public Discipline getDiscipline()
    {
        return Discipline.valueOf(getString("discipline"));
    }

    public void setDiscipline(Discipline discipline)
    {
        put("discipline",discipline.toString());
    }
}
