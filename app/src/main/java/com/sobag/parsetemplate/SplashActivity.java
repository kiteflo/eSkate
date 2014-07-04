package com.sobag.parsetemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.sobag.parsetemplate.services.InitializationListener;
import com.sobag.parsetemplate.services.ParseInitializationService;

import javax.annotation.Nullable;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContextSingleton
public class SplashActivity extends RoboActivity
        implements InitializationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    // service injection...
    @Inject
    ParseInitializationService parseInitializationService;

    @Nullable
    @InjectView(tag = "progressBar") ProgressBar progressBar;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init parse...
        parseInitializationService.init();
    }

    // ------------------------------------------------------------------------
    // parseService listener
    // ------------------------------------------------------------------------

    @Override
    public void handleStartInitialization()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleSuccessfulInitialization()
    {
        Ln.v("Parse initialized successfully!");

        // hide loading indicator...
        progressBar.setVisibility(View.GONE);

        boolean isVirgin = parseInitializationService.checkIfFirstTimeAccess();

        // first time access => Signup page
        if (isVirgin)
        {
            Intent launchActivitIntent = new Intent(this,LaunchActivity.class);
            startActivity(launchActivitIntent);
        }
        // login/login page
        else
        {
            if (parseInitializationService.getCurrentUser() == null)
            {
                // display launch pgae (signup/login dialog)
                Intent launchActivityIntent = new Intent(this, LaunchActivity.class);
                startActivity(launchActivityIntent);
            }
            else
            {
                // display main screen...
                Intent loginIntent = new Intent(this, MyRidesActivity.class);
                startActivity(loginIntent);
            }
        }

        finish();
    }

    @Override
    public void handleErrorDuringInitialization(Exception ex)
    {
        Ln.e(ex,"Parse initialization failed!");

        Intent errorIntent = new Intent(this,ErrorActivity.class);
        startActivity(errorIntent);

        finish();
    }
}
