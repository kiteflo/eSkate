package com.sobag.parsetemplate.services;

import android.content.Context;

import com.google.inject.Inject;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;

/**
 * Parse operations
 */
@ContextSingleton
public class ParseRequestService
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    @Inject
    RequestListener requestListener;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public ParseRequestService(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void fetchMyTracks()
    {
        requestListener.handleStartRequest();

        // TODO: implementation...
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------
}
