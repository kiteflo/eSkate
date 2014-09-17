package com.sobag.parsetemplate.services;

/**
 * Created by tzhmufl2 on 29.06.14.
 */
public interface LoginListener
{
    /**
     * Handle successful login...
     */
    public void handleSuccessfulLogin();

    /**
     * Sth. went wrong during login...
     * @param ex
     */
    public void handleLoginError(Exception ex);
}
