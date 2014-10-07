package com.sobag.parsetemplate.lists;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.RoundedImageView;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.Weapon;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import java.util.List;

public class WeaponListAdapter extends ArrayAdapter<Weapon>
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private final FontUtility fontUtility;
    private final Context context;
    private final List<Weapon> weapons;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public WeaponListAdapter(Context context, List<Weapon> weapons,
                             FontUtility fontUtility)
    {
        super(context, R.layout.cell_board, weapons);
        this.context = context;
        this.weapons = weapons;
        this.fontUtility = fontUtility;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        final Weapon weapon = weapons.get(position);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cell_board, parent, false);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.tv_title);
        tvTitle.setText(weapon.getTitle());

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        // setup image...
        final RoundedImageView ivImage = (RoundedImageView)rowView.findViewById(R.id.iv_image);

        String url = weapon.getImage().getUrl();
        Glide.with(context).load(url).into(ivImage);

        // wanna have some memory issues? then drop the following line..
        // board.resetImage();

        return rowView;
    }
}