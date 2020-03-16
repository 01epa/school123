package com.journal.nn.school123.fragment.messages;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.SwipeFragment;

public class MessagesFragment extends SwipeFragment {
    private MessageListener mListener;
    private RecyclerView view;

    public MessagesFragment() {
        super(R.layout.fragment_messages_list,
                R.id.message_swipe_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        view = rootView.findViewById(R.id.message_list);
        view.setLayoutManager(new LinearLayoutManager(context));
        return rootView;
    }

    public void update() {
        view.setAdapter(new MessagesRecyclerViewAdapter(mListener, userId, context));
        view.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessageListener) {
            mListener = (MessageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + MessageListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MessageListener {
        void onClick(MessageItem item);
    }
}
