package com.sobag.parsetemplate.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.domain.Ride;
import com.sobag.parsetemplate.domain.RideImage;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.GlobalUtility;

import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import roboguice.util.Ln;

public class RideListAdapter extends ArrayAdapter<Ride>
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private final FontUtility fontUtility;
    private final Context context;
    private final List<Ride> rides;
    private Bitmap defaultBitmap = null;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public RideListAdapter(Context context, List<Ride> rides,
                           FontUtility fontUtility)
    {
        super(context, R.layout.cell_ride_advanced, rides);
        this.context = context;
        this.rides = rides;
        this.fontUtility = fontUtility;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    public View getView(final int position, View view, ViewGroup parent)
    {
        final Ride ride = rides.get(position);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cell_ride_advanced, parent, false);

        // obsolete due to banner in photo...
        // title
        /*
        TextView tvTitle = (TextView)rowView.findViewById(R.id.tv_title);
        tvTitle.setText(ride.getCity() + " (" +ride.getCountry() +")");
        fontUtility.applyFontToComponent(tvTitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        // subtitle
        TextView tvSubtitle = (TextView)rowView.findViewById(R.id.tv_subtitle);
        tvSubtitle.setText(ride.getTitle());
        fontUtility.applyFontToComponent(tvSubtitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);*/

        // date
        TextView tvDate = (TextView)rowView.findViewById(R.id.tv_date);
        tvDate.setText(GlobalUtility.dateAndTimeFormat.format(ride.getRideDate()));
        fontUtility.applyFontToComponent(tvDate,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        ImageView ivMap = (ImageView)rowView.findViewById(R.id.iv_map);

        // get map URL
        String url = ride.getMapImage().getUrl();
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_map)
                .crossFade()
                .into(ivMap);

        return rowView;
    }
}