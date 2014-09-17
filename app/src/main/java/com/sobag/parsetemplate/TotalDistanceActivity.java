package com.sobag.parsetemplate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import roboguice.inject.InjectView;


public class TotalDistanceActivity extends CommonActivity
{
    @InjectView(tag = "tv_total_kilometers")
    TextView tvTotalKilometers;
    @InjectView(tag = "tv_total_kilometers_label")
    TextView tvTotalKilometersLabel;

    @Inject
    FontUtility fontUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_distance);

        // apply fonts
        fontUtility.applyFontToComponent(tvTotalKilometers,R.string.special_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvTotalKilometersLabel,R.string.special_font,
                FontApplicableComponent.TEXT_VIEW);

        String distance = getIntent().getExtras().getString("distance");
        tvTotalKilometers.setText(distance);
    }
}
