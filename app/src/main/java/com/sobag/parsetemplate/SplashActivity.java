package com.sobag.parsetemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Inject;
import com.sobag.parsetemplate.services.InitializationListener;
import com.sobag.parsetemplate.services.ParseInitializationService;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

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

    @InjectView(tag = "progressBar")
    ProgressBar progressBar = null;

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

        // SSL stuff...
        trustEveryone();

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

    /**
     * Enable self signed SSL certificates...
     */
    private void trustEveryone() {
        try {

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
            {
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException
                {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
