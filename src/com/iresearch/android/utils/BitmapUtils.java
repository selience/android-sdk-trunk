package com.iresearch.android.utils;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

public class BitmapUtils extends BitmapFactory {

    public static final String TAG = BitmapUtils.class.getSimpleName();

    
    public static Bitmap decodeUri(Context context,  Uri fileUri) {
        if (context == null || fileUri == null) {
            return null;
        }

        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getContentResolver().openInputStream(fileUri);
            bitmap = decodeStream(is);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } finally {
            closeStream(is);
        }

        return bitmap;
    }

    
    public static Bitmap decodeFile(String path, int minWidth, int minHeight) {
        Options options = null;

        if (minWidth > 0 || minHeight > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions
            options = new Options();
            options.inJustDecodeBounds = true;
            decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        return decodeFile(path, options);
    }

    
    public static Bitmap decodeResources(Resources res, int resId, int minWidth,
            int minHeight) {
        BitmapFactory.Options options = null;

        if (minWidth > 0 || minHeight > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        return decodeResource(res, resId, options);
    }


    public static void save(Bitmap bitmap,  Uri fileUri) {
        if (fileUri == null || bitmap == null) {
            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileUri.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            closeStream(out);
        }
    }

    public static Bitmap rotateBitmap(Uri fileUri, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(fileUri.getPath());
            int rotation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }

        } catch (IOException e) {
        	e.printStackTrace();
        }

        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int exifToDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
        }
        return 0;
    }

    private static void closeStream( Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options o, int minWidth, int minHeight) {
        // Raw height and width of image
        final int height = o.outHeight;
        final int width = o.outWidth;
        int inSampleSize = 1;

        if (height > minHeight || width > minWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > minHeight
                    && (halfWidth / inSampleSize) > minWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
