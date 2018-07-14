package com.andy.view.board;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class BoardView extends SurfaceView {
    public static final int MODE_NONE = -1;
    public static final int MODE_ZOOM = 0;
    public static final int MODE_DRAG = 1;

    private int current_status = MODE_NONE;
    /**
     * BoardView 被订阅的参数
     */
    public Board mBoard = new Board();

    /**
     * View 最初呈现时的原始边界值
     */
    protected int originalTop, originalBottom, originalLeft, originalRight;

    public BoardView(Context context) {
        super(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setState(int state) {
        current_status = state;
        mBoard.setState(state);
    }

    public int getStatus() {
        return current_status;
    }
}
