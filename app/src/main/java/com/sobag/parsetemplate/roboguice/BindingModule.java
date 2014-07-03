package com.sobag.parsetemplate.roboguice;

import com.google.inject.AbstractModule;
import com.sobag.parsetemplate.SplashActivity;
import com.sobag.parsetemplate.services.InitializationListener;

/**
 * RoboGuice interface wiring...
 */
public class BindingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(InitializationListener.class).to(SplashActivity.class);
    }
}