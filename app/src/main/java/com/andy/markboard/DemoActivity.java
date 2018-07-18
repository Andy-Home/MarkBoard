package com.andy.markboard;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.andy.view.action.StandardAction;
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
                board.setBoardContent(R.mipmap.test);
                board.showPointer(true);
            }
        });

        findViewById(R.id.fun1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PointF> data = board.getActionValues();
                mData.clear();
                if (data == null) {
                    return;
                }

                mData.addAll(data);
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
                board.putDefaultAction(mData);
            }
        });

        findViewById(R.id.fun4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board.createAction(new StandardAction());
            }
        });
    }
}
