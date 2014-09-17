package com.sobag.parsetemplate.services;

/**
 * Created by tzhmufl2 on 29.06.14.
 */
public interface SignupListener
{
    /**
     * Handle successful signup...
     */
    public void handleSuccessfulSignup();

    /**
     * Sth went wrong during initialization...
     */
    public void handleSignupError(Exception ex);
}
