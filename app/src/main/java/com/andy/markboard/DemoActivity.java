package com.andy.markboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.andy.view.board.StandardBoard;

public class DemoActivity extends Activity {

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

    }
}
