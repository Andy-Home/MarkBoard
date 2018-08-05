package com.andy.markboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.view.board.StandardBoard;

public class DemoFragment extends Fragment {

    private StandardBoard board;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_demo, null);
        board = view.findViewById(R.id.board);
        board.init(new StandardBoard.InitListener() {
            @Override
            public void onInit() {
                board.setBoardContent(R.mipmap.test);
                board.showPointer(true);
            }
        });
        return view;
    }
}
