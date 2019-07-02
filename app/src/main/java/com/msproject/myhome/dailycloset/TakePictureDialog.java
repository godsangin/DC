package com.msproject.myhome.dailycloset;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
    private Preview mPreview;
    ImageView takeButton;
    LinearLayout flowAfterView;
    Button retakeButton;
    Button saveButton;
    ImageView imageView;
    TextView dateTextView;

    Context context;
    LocalDate date;
    File file;
    boolean isExist;

    public TakePictureDialog(Context context) {
        super(context);
        this.context = context;
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
        saveButton = findViewById(R.id.save_bt);
        imageView = findViewById(R.id.imageview);
        dateTextView = findViewById(R.id.date_text);

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
        mPreview = new Preview(context, mCameraTextureView, convertFileName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPreview.setSurfaceTextureListener();
        }

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mPreview.takePicture();
                    flowAfterView.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });
        imageView.setVisibility(View.GONE);
    }

    private void startImageView(){
        imageView.setVisibility(View.VISIBLE);
        Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Pictures/DailyCloset/" + convertFileName() + ".jpg");
        imageView.setImageBitmap(bm);
        mCameraTextureView.setVisibility(View.GONE);
        takeButton.setVisibility(View.GONE);
        flowAfterView.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
    }

    private void setControllView(){

        retakeButton.setOnClickListener(new View.OnClickListener() {//다시찍기
            @Override
            public void onClick(View view) {
                if(mPreview == null){
                    startPreview();
                }
                else{
                    mPreview.startPreview();
                }
                flowAfterView.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사진 저장
                Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                saveButton.setVisibility(View.GONE);
            }
        });
        dateTextView.setText(convertFileName());
    }

}
