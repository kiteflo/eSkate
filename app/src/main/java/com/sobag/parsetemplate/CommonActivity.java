package com.sobag.parsetemplate;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sobag.parsetemplate.lists.NavigationListAdapter;
import com.sobag.parsetemplate.lists.items.NavigationListItem;
import com.sobag.parsetemplate.services.ParseLoginService;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;

/**
 * A place to put common activity operations such as hiding
 * title in action bar etc. - of our activities will extend this
 * class so adaptions will affect all activities.
 */
public class CommonActivity extends RoboActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    @Inject
    ParseLoginService parseLoginService;

    // sliding menu
    private SlidingMenu menu;

    // ------------------------------------------------------------------------
    // common stuff...
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // configure the SlidingMenu
        List<NavigationListItem> items = new ArrayList<NavigationListItem>();
        items.add(new NavigationListItem("Hello",R.drawable.ic_map));
        items.add(new NavigationListItem("World!",R.drawable.ic_launcher));

        View view = getLayoutInflater().inflate(R.layout.sidebar_menu, null);
        NavigationListAdapter nla = new NavigationListAdapter(getApplicationContext(),
                R.layout.cell_navigation,
                items);

        // fetch UI container and mixin contents...
        ListView lvCustomers = (ListView)view.findViewById(R.id.lv_menu);
        lvCustomers.setAdapter(nla);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.slidingmenuWidth);
        menu.setBehindOffsetRes(R.dimen.slidingmenuOffset);
        menu.setShadowWidth(R.dimen.shadowWidth);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(view);

        // action bar
        View aactionBarView = getLayoutInflater().inflate(R.layout.actionbar_plain, null);
        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(aactionBarView);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // actionbar listeners...need to be set explicitly...
        TextView yourTextView = (TextView)aactionBarView.findViewById(R.id.ab_title);
        yourTextView.setTextColor(getResources().getColor(R.color.white));

        String desiredFont = getString(R.string.second_font);
        Typeface typeface = Typeface.createFromAsset(getAssets(),desiredFont);
        yourTextView.setTypeface(typeface);

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
}
