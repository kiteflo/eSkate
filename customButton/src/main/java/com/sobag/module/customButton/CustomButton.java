package com.sobag.module.customButton;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by tzhmufl2 on 04.08.14.
 */
public class CustomButton extends RelativeLayout
{
    public CustomButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_button, this);

        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "buttonTitle");
        applyTitle(title);

        Boolean showIcon = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto", "showIcon", true);

        if (showIcon)
        {
            Drawable icon = getResources().getDrawable(attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "buttonIcon", R.drawable.world));
            applyIcon(icon);
        }
        else
        {
            applyIcon(null);
        }
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    private void applyTitle(String title)
    {
        TextView tvLabel = (TextView)findViewById(R.id.tv_label);
        tvLabel.setText(title);
    }

    private void applyIcon(Drawable icon)
    {
        ImageView ivIcon = (ImageView) findViewById(R.id.iv_icon);

        if (icon != null)
        {
            ivIcon.setImageDrawable(icon);
        }
        else
        {
            ivIcon.setVisibility(GONE);
        }
    }
}
