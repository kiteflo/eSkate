package com.sobag.parsetemplate.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sobag.parsetemplate.R;

/**
 * Common stuff used when dealing with SharedPreferences
 */
public class SharedPreferencesUtility
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Context context;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public SharedPreferencesUtility(Context context)
    {
        this.context = context;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Check whether user is first time accessor
     * @return
     * true in case of virgin access, else false will be returned.
     */
    public boolean checkIfFirstTimeAccess()
    {
        String filename = context.getString(R.string.preferencesFile);
        SharedPreferences preferences = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE);

        return preferences.getBoolean(PreferenceProps.IS_VIRGIN,true);
    }

    /**
     * Make this user a real man...by remnoving the virgin signup flag!
     */
    public void unvirginUser()
    {
        String filename = context.getString(R.string.preferencesFile);
        SharedPreferences preferences = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE);

        preferences.edit().putBoolean(PreferenceProps.IS_VIRGIN,false).commit();
    }
}
