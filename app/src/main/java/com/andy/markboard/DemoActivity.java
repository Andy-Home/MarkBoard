package com.andy.markboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.andy.view.ContentDialog;
import com.andy.view.action.Action;
import com.andy.view.action.StandardAction;
import com.andy.view.board.BoardView;
import com.andy.view.board.StandardBoard;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends Activity {
    private final String TAG = DemoActivity.class.getSimpleName();
    private List<Action> mData = new ArrayList<>();

    private ContentDialog vContentDialog;
    private StandardBoard vBoard;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vBoard = findViewById(R.id.board);
        vBoard.setBoardContent(R.mipmap.test);
        vBoard.showPointer(true);
        vBoard.showLabel(true);

        findViewById(R.id.fun1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Action> data = vBoard.getActionList();
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
                vBoard.clear();
            }
        });

        findViewById(R.id.fun3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vBoard.putActionValue(mData);
            }
        });

        findViewById(R.id.fun4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StandardAction action = vBoard.createAction(new StandardAction());
                vBoard.addAction(action);
            }
        });

        findViewById(R.id.fun5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, DemoFragmentActivity.class);
                startActivity(intent);
            }
        });

        vBoard.setActionLongClickListener(new BoardView.onActionLongClickListener() {
            @Override
            public void onLongClick(final int position) {
                final Action action = vBoard.getAction(position);
                if (TextUtils.isEmpty(action.getContent())) {
                    vContentDialog.show();
                    vContentDialog.setListener(new ContentDialog.Listener() {
                        @Override
                        public void onSure(String content) {
                            action.setContent(content);
                            vBoard.notifyDataChange();
                            vContentDialog.cancel();
                            vContentDialog.clearContent();
                        }

                        @Override
                        public void onCancel() {
                            vContentDialog.cancel();
                            vContentDialog.clearContent();
                        }
                    });
                }
            }
        });

        vBoard.setActionClickListener(new BoardView.onActionClickListener() {
            @Override
            public void onClick(int position) {
                StandardAction action = (StandardAction) vBoard.getAction(position);
                if (action.isShowContent()) {
                    action.hideContent();
                } else {
                    action.showContent(DemoActivity.this);
                }
            }
        });
    }
}
