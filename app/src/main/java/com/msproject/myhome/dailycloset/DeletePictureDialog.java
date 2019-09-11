package com.msproject.myhome.dailycloset;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class DeletePictureDialog extends Dialog {
    Context context;
    TextView dialogTitle;
    TextView dialogText;
    Button positiveButton;
    Button negativeButton;
    FileDeleteInteractionListener fileDeleteInteractionListener;

    public DeletePictureDialog(Context context, FileDeleteInteractionListener fileDeleteInteractionListener) {
        super(context);
        this.context = context;
        this.fileDeleteInteractionListener = fileDeleteInteractionListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.dialog_alert);


        dialogTitle = findViewById(R.id.title);
        dialogText = findViewById(R.id.alert_message);
        positiveButton = findViewById(R.id.positive_bt);
        negativeButton = findViewById(R.id.negative_bt);

        setLanguege();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileDeleteInteractionListener.deleteSucess();
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void setLanguege(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        int language = sharedPreferences.getInt("language", 0);
        switch (language){
            case 0:
                dialogTitle.setText(context.getString(R.string.delete_picture_dialog_title_EN));
                dialogText.setText(context.getString(R.string.delete_picture_dialog_text_EN));
                positiveButton.setText(context.getString(R.string.delete_picture_dialog_positive_EN));
                negativeButton.setText(context.getString(R.string.delete_picture_dialog_negative_EN));
                break;
            case 1:
                dialogTitle.setText(context.getString(R.string.delete_picture_dialog_title_KR));
                dialogText.setText(context.getString(R.string.delete_picture_dialog_text_KR));
                positiveButton.setText(context.getString(R.string.delete_picture_dialog_positive_KR));
                negativeButton.setText(context.getString(R.string.delete_picture_dialog_negative_KR));
                break;
            case 2:
                dialogTitle.setText(context.getString(R.string.delete_picture_dialog_title_JP));
                dialogText.setText(context.getString(R.string.delete_picture_dialog_text_JP));
                positiveButton.setText(context.getString(R.string.delete_picture_dialog_positive_JP));
                negativeButton.setText(context.getString(R.string.delete_picture_dialog_negative_JP));
                break;
        }

    }
}
