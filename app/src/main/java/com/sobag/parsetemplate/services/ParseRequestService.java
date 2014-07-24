package com.sobag.parsetemplate.services;

import android.content.Context;

import com.google.inject.Inject;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sobag.parsetemplate.domain.Board;

import java.util.List;

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

    /**
     * Fetch available board descriptions from server...
     * @param requestListener
     */
    public void fetchBoards(final RequestListener requestListener)
    {
        requestListener.handleStartRequest();

        ParseQuery<Board> query = ParseQuery.getQuery("Board");
        query.findInBackground(new FindCallback<Board>() {
            @Override
            public void done(List<Board> boards, ParseException e) {
                if (e == null) {
                    requestListener.handleRequestResult(boards);
                }
                else
                {
                    requestListener.handleParseRequestError(e);
                }
            }
        });
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
