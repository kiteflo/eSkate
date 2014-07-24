package com.sobag.parsetemplate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import roboguice.inject.InjectView;

public class RidesActivity extends CommonActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @InjectView(tag = "tv_new_ride_label")
    TextView tvNewRideButtonLabel;

    @Inject
    FontUtility fontUtility;

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        fontUtility.applyFontToComponent(tvNewRideButtonLabel,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void onCreateRide(View view)
    {
        Intent initRideActivity = new Intent(this,InitRideActivity.class);
        startActivity(initRideActivity);
    }
}
