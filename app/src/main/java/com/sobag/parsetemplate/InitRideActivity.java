package com.sobag.parsetemplate;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.lists.BoardListAdapter;
import com.sobag.parsetemplate.services.ParseLoginService;
import com.sobag.parsetemplate.services.ParseRequestService;
import com.sobag.parsetemplate.services.RequestListener;
import com.sobag.parsetemplate.util.FontUtility;

import java.util.List;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class InitRideActivity extends CommonActivity
    implements RequestListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Nullable
    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @InjectView(tag = "tv_board")
    TextView tvBoard;

    @Inject
    FontUtility fontUtility;

    @Inject
    ParseRequestService parseRequestService;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_ride);

        fontUtility.applyFontToComponent(tvBoard,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);

        // fetch boards...
        // display loading indicator...
        progressBar.setVisibility(View.VISIBLE);
        parseRequestService.fetchBoards(this);
    }

    // ------------------------------------------------------------------------
    // request handling...
    // ------------------------------------------------------------------------

    @Override
    public void handleStartRequest() {

    }

    @Override
    public void handleRequestResult(List result)
    {
        Ln.d("Result: " + result.size());
        // display loading indicator...
        progressBar.setVisibility(View.GONE);

        List<Board> boardList = (List<Board>)result;

        BoardListAdapter bla = new BoardListAdapter(getApplicationContext(),
                boardList, fontUtility);

        // fetch UI container and mixin contents...
        ListView lvBoards = (ListView)findViewById(R.id.lv_boards);
        lvBoards.setAdapter(bla);
    }

    @Override
    public void handleParseRequestError(Exception ex)
    {
        // display loading indicator...
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleParseRequestSuccess()
    {
        // display loading indicator...
        progressBar.setVisibility(View.GONE);
    }
}
