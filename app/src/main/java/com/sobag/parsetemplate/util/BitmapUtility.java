package com.sobag.parsetemplate.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.sobag.parsetemplate.domain.ClientUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.util.Ln;

/**
 * Created by tzhmufl2 on 27.07.14.
 */
public class BitmapUtility
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Context context;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public BitmapUtility(){}
    public BitmapUtility(Context context)
    {
        this.context = context;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Scale down image => 4 will return image scaled down to 1/4 width of
     * original etc.
     * @param scale
     * @return
     * @throws IOException
     */
    public Bitmap getDownsampledBitmap(File file, int scale)
            throws IOException
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

        if (bitmap != null)
        {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            float degree = getDegree(exifOrientation);
            if (degree != 0)
                bitmap = createRotatedBitmap(bitmap, degree);
        }

        return bitmap;
    }

    public Bitmap getDownsampledBitmap(Uri uri, int targetWidth, int targetHeight)
            throws IOException
    {
        Bitmap bitmap = null;
        try
        {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e)
        {
            Ln.e(e);
        }

        if (bitmap != null)
        {
            ExifInterface exif = new ExifInterface(uri.getPath());
            String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            float degree = getDegree(exifOrientation);
            if (degree != 0)
                bitmap = createRotatedBitmap(bitmap, degree);
        }

        return bitmap;
    }

    public static Bitmap createSquaredBitmap(Bitmap srcBmp)
    {
        Bitmap dstBmp;

        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

    public static Bitmap cropImageToCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        // paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    // ------------------------------------------------------------------------
    // private usage
    // ------------------------------------------------------------------------

    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException
    {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is= context.getContentResolver().openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight)
    {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth)
        {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException
    {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = context.getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }

    private float getDegree(String exifOrientation)
    {
        float degree = 0;
        if (exifOrientation.equals("6"))
            degree = 90;
        else if (exifOrientation.equals("3"))
            degree = 180;
        else if (exifOrientation.equals("8"))
            degree = 270;
        return degree;
    }

    private Bitmap createRotatedBitmap(Bitmap bm, float degree)
    {
        Bitmap bitmap = null;
        if (degree != 0)
        {
            Matrix matrix = new Matrix();
            matrix.preRotate(degree);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
