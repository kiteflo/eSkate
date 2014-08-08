package com.sobag.parsetemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sobag.parsetemplate.R;

import roboguice.inject.InjectView;

public class RideSummaryActivity extends CommonActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @InjectView(tag = "tv_title")
    TextView tvTitle;
    @InjectView(tag = "tv_date")
    TextView tvDate;

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_summary);

        // extract ride data
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");

        // apply values to UI
        tvTitle.setText(title);
        tvDate.setText(date);
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

}
