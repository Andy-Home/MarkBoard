package com.andy.view.board;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class BoardView extends SurfaceView {
    /**
     * BoardView 被订阅的参数
     */
    public Board mBoard;

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
}
