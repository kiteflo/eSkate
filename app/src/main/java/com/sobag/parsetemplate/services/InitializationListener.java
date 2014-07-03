package com.sobag.parsetemplate.services;

/**
 * Created by tzhmufl2 on 29.06.14.
 */
public interface InitializationListener
{
    /**
     * Start parse initialization...
     */
    public void handleStartInitialization();

    /**
     * Handle successful parse initialization.
     */
    public void handleSuccessfulInitialization();

    /**
     * Handle any error during parse initialization...
     * @param ex
     */
    public void handleErrorDuringInitialization(Exception ex);
}
