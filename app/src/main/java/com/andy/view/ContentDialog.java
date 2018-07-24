package com.andy.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andy.markboard.R;

/**
 * Created by Andy on 2018/7/24.
 * Modify time 2018/7/24
 */
public class ContentDialog extends Dialog {
    public ContentDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    private TextView vTitle;
    private EditText vContent;
    private Button vCancel, vSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_content);

        vTitle = findViewById(R.id.title);
        vContent = findViewById(R.id.content);

        vCancel = findViewById(R.id.cancle);
        vSure = findViewById(R.id.sure);

        setListener();
    }


    public void setTitle(String title) {
        vTitle.setText(title);
    }

    public void setCancel(String str) {
        vCancel.setText(str);
    }

    public void setSure(String str) {
        vSure.setText(str);
    }

    private void setListener() {
        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        vSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSure(vContent.getText().toString());
                }
            }
        });
    }

    public interface Listener {
        void onSure(String content);

        void onCancel();
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }
}
