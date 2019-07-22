package com.msproject.myhome.dailycloset;

import android.app.Dialog;
import android.content.Context;
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

        dialogTitle.setText("사진 삭제");
        dialogText.setText("해당 사진을 삭제하시겠습니까?");
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
}
