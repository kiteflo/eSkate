package com.sobag.parsetemplate.domain;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by tzhmufl2 on 03.07.14.
 */
@ParseClassName("_User")
public class User extends ParseUser
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private byte[] userImage;
    private String anotherField;

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getAnotherField() {
        return getString("anotherField");
    }

    public void setAnotherField(String anotherField) {
        put("anotherField", anotherField);
    }
}
