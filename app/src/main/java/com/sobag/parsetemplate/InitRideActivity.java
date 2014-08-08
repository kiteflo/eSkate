package com.sobag.parsetemplate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.makeramen.RoundedImageView;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.RideHolder;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.lists.BoardListAdapter;
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

    @Inject
    RideHolder rideHolder;

    @Inject
    FontUtility fontUtility;

    @Inject
    ParseRequestService parseRequestService;

    @InjectView(tag = "tv_board")
    TextView tvBoard;
    @InjectView(tag = "tv_label")
    TextView tvLabel;

    private View currentlySelectedItem = null;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_ride);

        // apply fonts
        fontUtility.applyFontToComponent(tvBoard,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        // fetch boards...
        parseRequestService.fetchBoards(this);

    }

    public void onNext(View view)
    {
        if (rideHolder.getBoard() == null)
        {
            Toast.makeText(this,getString(R.string.msg_select_board),Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent ridingActivityIntent = new Intent(this, RidingActivity.class);
            startActivity(ridingActivityIntent);
        }
    }

    // ------------------------------------------------------------------------
    // request handling...
    // ------------------------------------------------------------------------

    @Override
    public void handleStartRequest() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleRequestResult(List result)
    {
        Ln.d("Result: " + result.size());
        // display loading indicator...
        progressBar.setVisibility(View.GONE);

        final List<Board> boardList = (List<Board>)result;

        BoardListAdapter bla = new BoardListAdapter(getApplicationContext(),
                boardList, fontUtility);

        // fetch UI container and mixin contents...
        ListView lvBoards = (ListView)findViewById(R.id.lv_boards);
        lvBoards.setAdapter(bla);

        // add click handler...
        lvBoards.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int apiVersion = Build.VERSION.SDK_INT;

                if (currentlySelectedItem != null)
                {
                    TextView tvTitlePrevious = (TextView)currentlySelectedItem.findViewById(R.id.tv_title);
                    tvTitlePrevious.setAlpha(0.5f);
                    RoundedImageView ivImagePrevious = (RoundedImageView)currentlySelectedItem.findViewById(R.id.iv_image);
                    ivImagePrevious.setImageAlpha(120);
                    ivImagePrevious.setBorderColor(getResources().getColor(R.color.hint_grey));
                }

                TextView tvTitle = (TextView)view.findViewById(R.id.tv_title);
                tvTitle.setAlpha(1.0f);
                RoundedImageView ivImage = (RoundedImageView)view.findViewById(R.id.iv_image);
                ivImage.setImageAlpha(255);
                ivImage.setBorderColor(getResources().getColor(R.color.poisonGreen));

                currentlySelectedItem = view;

                // set board...
                Board board = boardList.get(position);
                rideHolder.setBoard(board);
            }
        });
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
