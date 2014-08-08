package com.sobag.parsetemplate.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        super(context, R.layout.cell_ride, rides);
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
        View rowView = inflater.inflate(R.layout.cell_ride, parent, false);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.tv_title);
        tvTitle.setText(ride.getTitle());
        TextView tvDate = (TextView)rowView.findViewById(R.id.tv_date);
        if (ride.getRideDate() != null)
        {
            tvDate.setText(GlobalUtility.dateFormat.format(ride.getRideDate()));
        }
        else
        {
            tvDate.setText("--.--.--");
        }
        TextView tvDistLabel = (TextView)rowView.findViewById(R.id.tv_dist_label);
        TextView tvDist = (TextView)rowView.findViewById(R.id.tv_dist);
        tvDist.setText(GlobalUtility.decimalFormat.format(ride.getDistance()/1000));
        TextView tvMaxLabel = (TextView)rowView.findViewById(R.id.tv_max_label);
        TextView tvMax = (TextView)rowView.findViewById(R.id.tv_max);
        tvMax.setText(GlobalUtility.decimalFormat.format(ride.getMaxSpeed()));
        TextView tvAvgLabel = (TextView)rowView.findViewById(R.id.tv_avg_label);
        TextView tvAvg = (TextView)rowView.findViewById(R.id.tv_avg);
        tvAvg.setText(GlobalUtility.decimalFormat.format(ride.getAvgSpeed()));
        TextView tvTimeLabel = (TextView)rowView.findViewById(R.id.tv_time_label);
        TextView tvTime = (TextView)rowView.findViewById(R.id.tv_time);
        tvTime.setText(ride.getDuration());

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvDate,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvDistLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvDist,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvMaxLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvMax,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvAvgLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvAvg,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvTimeLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvTime,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        // setup image...
        final RoundedImageView ivImage = (RoundedImageView)rowView.findViewById(R.id.iv_image);

        if (ride.getImages().size() == 0)
        {
            // we have to fetch the images...
            if (ride.getRideImages() != null)
            {
                ParseQuery<RideImage> query = ride.getRideImages().getQuery();
                query.findInBackground(new FindCallback<RideImage>()
                {
                    @Override
                    public void done(List<RideImage> rideImages, ParseException e)
                    {
                        if (e == null)
                        {
                            ride.setImages(rideImages);

                            if (ride.getImages() != null && ride.getImages().size() > 0)
                            {
                                ride.getImages().get(0).getRideImage().getDataInBackground(new GetDataCallback()
                                {
                                    @Override
                                    public void done(byte[] bytes, ParseException e)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
                                        ride.getImages().get(0).setThumbnail(bitmap);
                                        ride.getImages().get(0).setRawData(bytes);
                                        ivImage.setImageBitmap(bitmap);

                                        // reset
                                        ride.getImages().get(0).resetRideImage();

                                        rides.set(position,ride);
                                    }
                                });
                            }
                            else
                            {
                                if (defaultBitmap == null)
                                {
                                    defaultBitmap = BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.no_photo);
                                }

                                ivImage.setImageBitmap(defaultBitmap);

                                rides.set(position,ride);
                            }
                        } else
                        {
                            Ln.e(e);
                        }
                    }
                });
            }
        }

        else if (ride.getImages() != null && ride.getImages().size() > 0 && ride.getImages().get(0).getThumbnail() != null)
        {
            ivImage.setImageBitmap(ride.getImages().get(0).getThumbnail());
        }

        return rowView;
    }
}