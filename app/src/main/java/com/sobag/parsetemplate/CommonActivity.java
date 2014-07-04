package com.sobag.parsetemplate;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.services.ParseLoginService;

import roboguice.activity.RoboActivity;

/**
 * A place to put common activity operations such as hiding
 * title in action bar etc. - of our activities will extend this
 * class so adaptions will affect all activities.
 */
public class CommonActivity extends RoboActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Inject
    ParseLoginService parseLoginService;

    // ------------------------------------------------------------------------
    // common stuff...
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // apply custom font to action bar...
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));

        String desiredFont = getString(R.string.default_font);
        Typeface typeface = Typeface.createFromAsset(getAssets(),desiredFont);
        yourTextView.setTypeface(typeface);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.action_logout)
        {
            parseLoginService.logout(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
