package com.msproject.myhome.dailycloset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

public class ImageDownloader {
    Context context;
    public ImageDownloader(Context context){
        this.context = context;
    }
    public void download(String url, ImageView imageView){
        if(cancelPotentialDownload(url, imageView)){
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.execute(url);
        }
    }

    public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        String url;
        private final WeakReference<ImageView> imageViewWeakReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()){
                bitmap = null;
            }
            if(imageViewWeakReference != null){
                ImageView imageView = imageViewWeakReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                if(this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            super.onPostExecute(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadBitmap(strings[0]);
        }

        public Bitmap downloadBitmap(final String imgURL){
            final Bitmap[] bm = new Bitmap[1];
            Thread thread = new Thread(){
                @Override
                public void run() {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imgURL, options);
                    int sampleSize = getSampliSize(options.outWidth, options.outHeight);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = sampleSize;

                    bm[0] = BitmapFactory.decodeFile(imgURL, options);
                    bm[0] = rotateBitmap(bm[0], imgURL);
                }
            };
            thread.start();
            try{
                thread.join();
                return bm[0];
            }catch (InterruptedException e){
                e.printStackTrace();
                Log.d("쓰레드 인터럽트==", "발생");
            }
            return bm[0];
        }

        private Bitmap rotateBitmap(Bitmap bitmap, String filepath){
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(filepath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                Matrix matrix = new Matrix();
                switch (orientation){
                    case ExifInterface.ORIENTATION_NORMAL:
                        return bitmap;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        matrix.setScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.setRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        matrix.setRotate(180);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_TRANSPOSE:
                        matrix.setRotate(90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.setRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_TRANSVERSE:
                        matrix.setRotate(-90);
                        matrix.postScale(-1,1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270://전면카메라일 경우 Preview에서 설정한 orientation = 270이 되기 때문에 좌우반전도 포함시킨다.
                        matrix.setRotate(-90);
                        matrix.postScale(-1,1);
                        break;
                    default:
                        return bitmap;
                }
                try{
                    Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    return bmRotated;
                }catch (OutOfMemoryError e){
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }
        private int getSampliSize(int width, int height) {
            // 화면 크기 취득
            Display currentDisplay = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            float dw = currentDisplay.getWidth();
            float dh = currentDisplay.getHeight();
            // 가로/세로 축소 비율 취득
            int widthtRatio = (int) Math.ceil(width / dw);
            int heightRatio = (int) Math.ceil(height / dh);
            // 초기 리사이즈 비율
            int sampleSize = 1;
            // 가로 세로 비율이 화면보다 큰경우에만 처리
            if (widthtRatio > 1 && height > 1) {
                if (widthtRatio > heightRatio) {
                    // 가로 축소 비율이 큰 경우
                    sampleSize = widthtRatio;
                } else {
                    // 세로 축소 비율이 큰 경우
                    sampleSize = heightRatio;
                }
            }
            return sampleSize;
        }
    }


    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskWeakReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.WHITE);
            this.bitmapDownloaderTaskWeakReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskWeakReference.get();
        }
    }
    public static boolean cancelPotentialDownload(String url, ImageView imageView){
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if(bitmapDownloaderTask != null){
            String bitmapURL = bitmapDownloaderTask.url;
            if((bitmapURL == null) || (!bitmapURL.equals(url))){
                bitmapDownloaderTask.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView){
        if(imageView != null){
            Drawable drawable = imageView.getDrawable();
            if(drawable instanceof DownloadedDrawable){
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }
}
