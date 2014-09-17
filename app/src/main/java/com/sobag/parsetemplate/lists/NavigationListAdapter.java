package com.sobag.parsetemplate.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.lists.items.NavigationListItem;

import java.util.List;

/**
 * Created by tzhmufl2 on 03.09.14.
 */
public class NavigationListAdapter extends ArrayAdapter<NavigationListItem>
{
    private final Context context;
    private final List<NavigationListItem> navItems;

    public NavigationListAdapter(Context context, int textViewResourceId, List<NavigationListItem> navItems)
    {
        super(context, R.layout.cell_navigation, navItems);
        this.context = context;
        this.navItems = navItems;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        NavigationListItem navItem = navItems.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cell_navigation, parent, false);

        TextView tvLabel = (TextView)rowView.findViewById(R.id.tv_label);
        ImageView ivImage = (ImageView)rowView.findViewById(R.id.iv_image);

        tvLabel.setText(navItem.getLabel());
        ivImage.setImageResource(navItem.getDrawable());

        return rowView;
    }
}