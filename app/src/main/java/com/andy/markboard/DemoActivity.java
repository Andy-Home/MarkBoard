package com.andy.markboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.andy.view.board.StandardBoard;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends Activity {
    private final String TAG = DemoActivity.class.getSimpleName();
    private List<PointF> mData = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final StandardBoard board = findViewById(R.id.board);
        board.init(new StandardBoard.InitListener() {
            @Override
            public void onInit() {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                board.setBackground(bitmap);
            }
        });

        findViewById(R.id.fun1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PointF> data = board.getActionValue();
                mData.clear();
                mData.addAll(data);
                for (PointF point : data) {
                    Log.i(TAG, "x:" + point.x + " y:" + point.y);
                }
            }
        });

        findViewById(R.id.fun2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.clear();
            }
        });

        findViewById(R.id.fun3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.putActionValue(mData);
            }
        });
    }
}
