package com.sobag.parsetemplate.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseFile;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.Badge;
import com.sobag.parsetemplate.domain.Condition;

import java.io.ByteArrayOutputStream;

/**
 * Created by tzhmufl2 on 03.10.14.
 */
public class DataGenerationUtility
{
    /**
     * Badge generation...
     */
    public static void generateBadges(Context context)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Badge badge = new Badge();
        badge.setTitle("first ride!");
        badge.setTitle("your first ride on an electric vehicle");
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.badge_1);
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile parseBadgeImage = new ParseFile("first_ride.png",byteArray);
        badge.setImage(parseBadgeImage);

        // add conditions...
        Condition condition = new Condition();
        condition.setDescription("First time eSkate activity...");
        condition.setKey("RIDE");
        condition.setMinValue("1");
        condition.saveInBackground();
        badge.getConditions().add(condition);
        badge.saveInBackground();

        badge = new Badge();
        badge.setTitle("sppedy (bronze)");
        badge.setTitle("speed > 20 km/h");
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.badge_2);
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        parseBadgeImage = new ParseFile("speedy_bronze.png",byteArray);
        badge.setImage(parseBadgeImage);

        // add conditions...
        condition = new Condition();
        condition.setDescription("speed >= 20km/h");
        condition.setKey("SPEED");
        condition.setMinValue("20");
        condition.saveInBackground();
        badge.getConditions().add(condition);
        badge.saveInBackground();

        badge = new Badge();
        badge.setTitle("speedy (silver)");
        badge.setTitle("speed > 30 km/h");
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.badge_3);
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        parseBadgeImage = new ParseFile("speedy_silver.png",byteArray);
        badge.setImage(parseBadgeImage);

        // add conditions...
        condition = new Condition();
        condition.setDescription("speed >= 30km/h");
        condition.setKey("SPEED");
        condition.setMinValue("30");
        condition.saveInBackground();
        badge.getConditions().add(condition);
        badge.saveInBackground();

        badge = new Badge();
        badge.setTitle("speedy (gold)");
        badge.setTitle("speed > 40 km/h");
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.badge_4);
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        parseBadgeImage = new ParseFile("speedy_gold.png",byteArray);
        badge.setImage(parseBadgeImage);

        // add conditions...
        condition = new Condition();
        condition.setDescription("speed >= 40km/h");
        condition.setKey("SPEED");
        condition.setMinValue("40");
        condition.saveInBackground();
        badge.getConditions().add(condition);
        badge.saveInBackground();
    }
}
