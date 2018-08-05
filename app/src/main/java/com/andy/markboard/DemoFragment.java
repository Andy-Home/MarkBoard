package com.andy.markboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.view.board.StandardBoard;

public class DemoFragment extends Fragment {

    private StandardBoard vBoard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_demo, null);
        vBoard = view.findViewById(R.id.board);
        vBoard.setBoardContent(R.mipmap.test);
        vBoard.showPointer(true);
        vBoard.showLabel(true);
        return view;
    }
}
