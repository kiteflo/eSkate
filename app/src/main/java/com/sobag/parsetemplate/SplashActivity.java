package com.sobag.parsetemplate;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Inject;
import com.sobag.parsetemplate.services.InitializationListener;
import com.sobag.parsetemplate.services.ParseInitializationService;
import com.sobag.parsetemplate.util.DataGenerationUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private ProgressBar progressBar = null;


    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // crashlytics?
        // Crashlytics.start(this);

        // init parse...
        parseInitializationService.init();

        // progressbar...needs special treatment
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // --------------------------------------------------------------------
        // EXPERIMENTAL & DATA GENERATION SECTION
        // --------------------------------------------------------------------

        // printKeyHash();
        DataGenerationUtility.generateBadges(this);
    }

    // ------------------------------------------------------------------------
    // parseService listener
    // ------------------------------------------------------------------------

    @Override
    public void handleStartInitialization()
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleSuccessfulInitialization()
    {
        Ln.v("Parse initialized successfully!");

        // hide loading indicator...
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }

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
                Intent loginIntent = new Intent(this, RidesActivity.class);
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

    private void printKeyHash(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sobag.parsetemplate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }
    }
}
