package com.sobag.parsetemplate;

import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;

@ContextSingleton
public class ErrorActivity extends CommonHeadlessActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Inject
    FontUtility fontUtility;

    @InjectView(tag = "tv_title") TextView tvTitle;
    @InjectView(tag = "tv_subtitle") TextView tvSubtitle;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.second_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvSubtitle,R.string.second_font,
                FontApplicableComponent.TEXT_VIEW);
    }
}
