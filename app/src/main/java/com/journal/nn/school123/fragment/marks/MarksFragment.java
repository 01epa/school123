package com.journal.nn.school123.fragment.marks;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.SwipeFragment;
import com.journal.nn.school123.pojo.Period;

public class MarksFragment extends SwipeFragment {
    private MarksListener mListener;
    private RecyclerView view;
    private Period period;

    public MarksFragment() {
        super(R.layout.fragment_marks_list,
                R.id.marks_swipe_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        view = rootView.findViewById(R.id.marks_list);
        view.setLayoutManager(new LinearLayoutManager(context));
        return rootView;
    }

    public void update() {
        view.setAdapter(new MarksRecyclerViewAdapter(mListener,
                period,
                userId,
                context));
        view.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MarksListener) {
            mListener = (MarksListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + MarksListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPeriod(@NonNull Period period) {
        this.period = period;
    }

    public interface MarksListener {
        void onClick(MarksItem item);
    }
}
