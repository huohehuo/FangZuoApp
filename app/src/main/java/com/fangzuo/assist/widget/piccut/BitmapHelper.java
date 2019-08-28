package com.fangzuo.assist.widget.piccut;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class BitmapHelper {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            final float roundPx = bitmap.getWidth() / 2;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static Bitmap scale(Bitmap image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            Log.e("BitmapHelper", e.getMessage());
        }
        return null;
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        if (bm == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static boolean saveBitmap(String path, Bitmap image) {
        if (image == null) return false;
        File f = new File(path);
        File father = f.getParentFile();
        if (father == null) {
            Log.e("saveBitmap", "unknown error with path = " + path);
            return false;
        }
        if (!father.exists() && !father.mkdirs()) {
            Log.e("saveBitmap", "can not create file " + path);
            return false;
        }
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            Log.e("saveBitmap", e.getMessage());
            return false;
        }
        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取图片
     * @param path 文件路径
     * @param maxSize 最大宽或高
     * @param rotate 旋转角度
     * @return 图片
     */
    public static Bitmap readBitmap(String path, int maxSize, int rotate) {
        File file = new File(path);
        if (!file.exists()) return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 计算图片缩放比例
        int max = Math.max(opts.outWidth, opts.outHeight);
        opts.inSampleSize = 1;
        while (max > maxSize) {
            opts.inSampleSize = opts.inSampleSize * 2;
            max = max / 2;
        }
        opts.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(path, opts);
        if (rotate == 0) return image;
        Bitmap toReturn = rotateImage(rotate, image);
        image.recycle();
        return toReturn;
    }

    /**
     * 读取图片
     * @param path 文件路径
     * @param maxPix 最大像素（宽乘以高）
     * @param rotate 旋转角度
     * @return 图片
     */
    public static Bitmap readBitmapByMaxPix(String path, long maxPix, int rotate) {
        File file = new File(path);
        if (!file.exists()) return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 计算图片缩放比例
        long max = opts.outWidth * opts.outHeight;
        opts.inSampleSize = 1;
        while (max > maxPix) {
            opts.inSampleSize = opts.inSampleSize * 2;
            max = max / 4;
        }
        opts.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(path, opts);
        if (rotate == 0) return image;
        Bitmap toReturn = rotateImage(rotate, image);
        image.recycle();
        return toReturn;
    }

    /**
     * Uri 转 绝对路径
     * @param context context
     * @param uri     uri
     * @return 绝对路径
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) data = uri.getPath();
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String path = uri.getPath();
            String id = "";
            for (int i = path.length() - 1; i > 0 && path.charAt(i) >= '0' && path.charAt(i) <= '9'; i--) id = "" + path.charAt(i) + id;
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns._ID}
                    , MediaStore.Images.ImageColumns._ID + "=" + id, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) data = cursor.getString(index);
                }
                cursor.close();
            }
        }
        return data;
    }
}
