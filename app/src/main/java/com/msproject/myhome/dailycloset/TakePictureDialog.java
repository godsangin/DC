package com.msproject.myhome.dailycloset;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.camera.core.PreviewConfig;
import org.joda.time.LocalDate;

import java.io.File;

public class TakePictureDialog extends Dialog {
    private TextureView mCameraTextureView;
    private Preview2 mPreview;
    ImageView takeButton;
    LinearLayout flowAfterView;
    Button retakeButton;
    Button closeButton;
    ImageView imageView;
    ImageView rotate_bt;
    TextView dateTextView;
    TextView alert_text;

    Context context;
    LocalDate date;
    File file;
    boolean isExist;
    FileSaveNotifyListener fileSaveNotifyListener;

    public TakePictureDialog(Context context, FileSaveNotifyListener fileSaveNotifyListener) {
        super(context);
        this.context = context;
        this.fileSaveNotifyListener = fileSaveNotifyListener;
    }

    public void setDate(LocalDate localDate){
        this.date = localDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.dialog_takepicture);

        mCameraTextureView = findViewById(R.id.textureview);
        takeButton = findViewById(R.id.take_bt);
        flowAfterView = findViewById(R.id.flow_after_view);
        retakeButton = findViewById(R.id.retake_bt);
        closeButton = findViewById(R.id.close_bt);
        imageView = findViewById(R.id.imageview);
        dateTextView = findViewById(R.id.date_text);
        rotate_bt = findViewById(R.id.rotate_bt);
        alert_text = findViewById(R.id.alert_text);
        setLanguege();
        file = new File(Environment.getExternalStorageDirectory()+"/Pictures/DailyCloset", convertFileName() + ".jpg");
        if(!file.canRead()){
            isExist = false;
            startPreview();
        }
        else{
            isExist = true;
            startImageView();
        }
        setControllView();
        setLanguege();
    }

    public String convertFileName(){
        String str = this.date.toString("yyyyMMdd");
        return str;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//바깥 뷰 클릭 시 다이얼로그를 재활용하지 않고 dismiss시킨다.
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if(!dialogBounds.contains((int) ev.getX(), (int)ev.getY())){
            if(mPreview != null){
                mPreview.onPause();
            }
            dismiss();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startPreview(){
        mCameraTextureView.setVisibility(View.VISIBLE);
        takeButton.setVisibility(View.VISIBLE);
        rotate_bt.setVisibility(View.VISIBLE);
        alert_text.setVisibility(View.VISIBLE);
        mPreview = new Preview2(context, mCameraTextureView, convertFileName());
        mPreview.startBackgroundThread();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mPreview.setSurfaceTextureListener();
//        }

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mPreview.takePicture();
                    flowAfterView.setVisibility(View.VISIBLE);
                    closeButton.setVisibility(View.VISIBLE);
                    rotate_bt.setVisibility(View.GONE);
                    alert_text.setVisibility(View.GONE);
                    findViewById(R.id.close_container).setVisibility(View.VISIBLE);
                    if(fileSaveNotifyListener != null) {
                        fileSaveNotifyListener.notifyDatasetChanged();
                    }
                }
            }
        });
        rotate_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.onPause();
                mPreview.toggle();
                Log.d("rotate==", "true");
                mPreview.onResume();
            }
        });
        imageView.setVisibility(View.GONE);
    }

    private void startImageView(){
        imageView.setVisibility(View.VISIBLE);
        //회전각도에 따른 비율 설정
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int sampleSize = getSampliSize(options.outWidth, options.outHeight);
        Log.d("width==", options.outWidth + "height==" + options.outHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;

        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        bm = rotateBitmap(bm, file.getAbsolutePath());
//        bm = getRotatedBitmap(bm, degrees);
//        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
        imageView.setImageBitmap(bm);
        mCameraTextureView.setVisibility(View.GONE);
        takeButton.setVisibility(View.GONE);
        flowAfterView.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);
        rotate_bt.setVisibility(View.GONE);
        alert_text.setVisibility(View.GONE);
        findViewById(R.id.close_container).setVisibility(View.GONE);
    }

    private void setControllView(){

        retakeButton.setOnClickListener(new View.OnClickListener() {//다시찍기
            @Override
            public void onClick(View view) {
                if(mPreview == null){
                    startPreview();
                }
                else{
                    mPreview.createCameraPreview();
                }
                flowAfterView.setVisibility(View.GONE);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사진 저장
                if(mPreview != null){
                    mPreview.onPause();
                }
                dismiss();
            }
        });
        dateTextView.setText(convertFileName());
    }

    private int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch(orientation) {
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
            }
        }
        return degree;
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

    private void setLanguege(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        int language = sharedPreferences.getInt("language", 0);
        switch(language){
            case 0:
                alert_text.setText(context.getString(R.string.dialog_take_picture_alert_EN));
                retakeButton.setText(context.getString(R.string.dialog_take_picture_retake_EN));
                closeButton.setText(context.getString(R.string.dialog_take_picture_close_EN));
                break;
            case 1:
                alert_text.setText(context.getString(R.string.dialog_take_picture_alert_KR));
                retakeButton.setText(context.getString(R.string.dialog_take_picture_retake_KR));
                closeButton.setText(context.getString(R.string.dialog_take_picture_close_KR));
                break;
            case 2:
                alert_text.setText(context.getString(R.string.dialog_take_picture_alert_JP));
                retakeButton.setText(context.getString(R.string.dialog_take_picture_retake_JP));
                closeButton.setText(context.getString(R.string.dialog_take_picture_close_JP));
                break;
        }

    }
}
