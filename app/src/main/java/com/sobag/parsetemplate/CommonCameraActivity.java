package com.sobag.parsetemplate;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.services.ParseLoginService;

import roboguice.activity.RoboActivity;

/**
 * A place to put common activity operations such as hiding
 * title in action bar etc. - of our activities will extend this
 * class so adaptions will affect all activities.
 */
public class CommonCameraActivity extends RoboActivity
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

        // custom action bar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        View cView = getLayoutInflater().inflate(R.layout.actionbar_camera, null);
        actionBar.setCustomView(cView);

        TextView yourTextView = (TextView) findViewById(R.id.ab_title);
        yourTextView.setTextColor(getResources().getColor(R.color.white));

        String desiredFont = getString(R.string.second_font);
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