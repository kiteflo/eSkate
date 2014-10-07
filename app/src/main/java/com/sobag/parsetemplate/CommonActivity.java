package com.sobag.parsetemplate;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.google.inject.Inject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.makeramen.RoundedImageView;
import com.parse.ParseUser;
import com.sobag.parsetemplate.domain.ClientUser;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.lists.NavigationListAdapter;
import com.sobag.parsetemplate.lists.items.NavigationListItem;
import com.sobag.parsetemplate.services.ParseInitializationService;
import com.sobag.parsetemplate.services.ParseLoginService;
import com.sobag.parsetemplate.util.FontUtility;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContextSingleton;

/**
 * A place to put common activity operations such as hiding
 * title in action bar etc. - of our activities will extend this
 * class so adaptions will affect all activities.
 */
@ContextSingleton
public class CommonActivity extends RoboActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Inject
    ParseLoginService parseLoginService;
    @Inject
    ParseInitializationService parseInitService;

    @Inject
    FontUtility fontUtility;

    @Inject
    ClientUser clientUser;

    private CommonActivity selfReference;

    // action bar
    private TextView tvActionBar;

    // sliding menu
    private SlidingMenu menu;
    private TextView tvUser;
    private TextView tvLogout;
    private TextView tvRideNow;
    private TextView tvMyRides;
    private TextView tvTotal;
    private TextView tvAchievements;
    private ImageView ivUser;
    private ProfilePictureView fbImage;

    // buttons
    private RelativeLayout rlRideNow;
    private RelativeLayout rlTotalDistance;
    private RelativeLayout rlMyTracks;
    private RelativeLayout rlLogout;

    // ------------------------------------------------------------------------
    // common stuff...
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        selfReference = this;

        // configure the SlidingMenu
        View view = getLayoutInflater().inflate(R.layout.sidebar_menu, null);
        // apply dynamic menu properties (user image, username etc.)
        tvUser = (TextView)view.findViewById(R.id.tv_user);
        tvLogout = (TextView)view.findViewById(R.id.tv_logout);
        tvRideNow = (TextView)view.findViewById(R.id.tv_ride_now);
        tvAchievements = (TextView)view.findViewById(R.id.tv_myachievements);
        tvTotal = (TextView)view.findViewById(R.id.tv_total);
        tvMyRides = (TextView)view.findViewById(R.id.tv_myrides);
        ivUser = (RoundedImageView)view.findViewById(R.id.iv_image);

        // action areas
        rlRideNow = (RelativeLayout)view.findViewById(R.id.rl_rideNow);
        rlRideNow.setOnClickListener(new RideNowAdapter());
        rlTotalDistance = (RelativeLayout)view.findViewById(R.id.rl_totalDistance);
        rlTotalDistance.setOnClickListener(new TotalKilometersAdapter());
        rlMyTracks = (RelativeLayout)view.findViewById(R.id.rl_myTracks);
        rlMyTracks.setOnClickListener(new MyTracksAdapter());
        rlLogout = (RelativeLayout)view.findViewById(R.id.rl_logout);
        rlLogout.setOnClickListener(new LogoutAdapter());


        // apply fonts
        fontUtility.applyFontToComponent(tvUser,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvMyRides,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvTotal,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvAchievements,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvLogout,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvRideNow,R.string.special_font,
                FontApplicableComponent.TEXT_VIEW);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.slidingmenuWidth);
        menu.setBehindOffsetRes(R.dimen.slidingmenuOffset);
        menu.setShadowWidth(R.dimen.shadowWidth);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(view);
        menu.setOnOpenListener(new SlidingMenu.OnOpenListener()
        {
            @Override
            public void onOpen()
            {
                if (tvUser.getText() == null ||(tvUser.getText() != null && tvUser.getText().toString().trim().length() == 0))
                {
                    tvUser.setText(clientUser.getFirstname() + " " +clientUser.getLastname());
                    ivUser.setImageBitmap(clientUser.getUserImage());
                    tvTotal.setText(getString(R.string.item_total,String.format("%.2f", clientUser.getTotalDistanceInMeters() / 1000)+" km"));
                }

                // apply rounded image
                ImageView iv = ((ImageView)fbImage.getChildAt(0));
                Bitmap bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
                ivUser.setImageBitmap(bitmap);
            }
        });

        // action bar
        View aactionBarView = getLayoutInflater().inflate(R.layout.actionbar_plain, null);
        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(aactionBarView);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // actionbar listeners...need to be set explicitly...
        tvActionBar = (TextView)aactionBarView.findViewById(R.id.ab_title);
        fontUtility.applyFontToComponent(tvActionBar,R.string.special_font,
                FontApplicableComponent.TEXT_VIEW);
        /*String desiredFont = getString(R.string.second_font);
        Typeface typeface = Typeface.createFromAsset(getAssets(),desiredFont);
        yourTextView.setTypeface(typeface);*/

        fbImage = (ProfilePictureView)aactionBarView.findViewById(R.id.fb_image);

        // fetch facebook user...
        // init client user...fetch image etc...
        parseInitService.initClientUser(fbImage);

        ImageView iv = (ImageView)aactionBarView.findViewById(R.id.ab_icon);
        iv.setOnClickListener(new ActionBarButtonCLickListener());

        View clickArea = (View)aactionBarView.findViewById(R.id.view_clickarea);
        clickArea.setOnClickListener(new ActionBarButtonCLickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.action_logout)
        {
            parseLoginService.logout(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------------
    // action handlers
    // ------------------------------------------------------------------------

    private class RideNowAdapter implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            menu.toggle(true);

            Intent initRideActivity = new Intent(selfReference,InitRideActivity.class);
            startActivity(initRideActivity);
        }
    }

    private class TotalKilometersAdapter implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            menu.toggle(true);

            Intent showKilometersIntent = new Intent(selfReference,TotalDistanceActivity.class);
            showKilometersIntent.putExtra("distance",String.format("%.2f", clientUser.getTotalDistanceInMeters() / 1000));
            startActivity(showKilometersIntent);
        }
    }

    private class MyTracksAdapter implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            menu.toggle(true);

            Intent showTrackIntent = new Intent(selfReference,RidesActivity.class);
            startActivity(showTrackIntent);
        }
    }

    private class LogoutAdapter implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            menu.toggle(true);

            ParseUser.logOut();

            Intent launchActivity = new Intent(selfReference,LaunchActivity.class);
            startActivity(launchActivity);
        }
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    private class ActionBarButtonCLickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            menu.toggle(true);
        }
    }

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------


    public TextView getTvActionBar()
    {
        return tvActionBar;
    }

    public void setTvActionBar(TextView tvActionBar)
    {
        this.tvActionBar = tvActionBar;
    }
}
