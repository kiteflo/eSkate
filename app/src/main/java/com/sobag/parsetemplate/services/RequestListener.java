package com.sobag.parsetemplate.services;

/**
 * Created by tzhmufl2 on 29.06.14.
 */
public interface RequestListener
{
    /**
     * Indicate new request action, something is fetched/pushed to the server...
     */
    public void handleStartRequest();

    /**
     * Handle any exception which might occur during parse operation...
     * @param ex
     */
    public void handleParseRequestError(Exception ex);

    /**
     * Indicate/handle successfuly parse operation...
     */
    public void handleParseRequestSuccess();
}
