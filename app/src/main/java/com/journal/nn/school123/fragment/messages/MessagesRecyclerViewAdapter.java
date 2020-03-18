package com.journal.nn.school123.fragment.messages;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journal.nn.school123.R;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Message;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {
    private static final DateFormatSymbols SYMBOLS = new DateFormatSymbols();
    private final List<MessageItem> messageItems;
    private final MessagesFragment.MessageListener mListener;
    private String userId;

    public MessagesRecyclerViewAdapter(@NonNull MessagesFragment.MessageListener listener,
                                       @NonNull String userId,
                                       @NonNull Context context) {
        this.userId = userId;
        messageItems = initMessageItems(context);
        mListener = listener;
    }

    @NonNull
    private ArrayList<MessageItem> initMessageItems(@NonNull Context context) {
        ArrayList<MessageItem> messageItems = new ArrayList<>();
        Data data = IntentHelper.getData(context, userId);
        Map<Integer, String> teachers = data.getTeachers();
        List<Message> messages = data.getMessages();
        messageItems.add(initHeaders());
        messages.forEach(message -> {
            Date date = message.getDate();
            String dateText = date.getDate() + " " + SYMBOLS.getMonths()[date.getMonth()] + " " + (date.getYear() + 1900);
            MessageItem messageItem = new MessageItem(dateText,
                    teachers.get(message.getTeacherId()),
                    message.getMessage(),
                    false);
            messageItems.add(messageItem);
        });
        return messageItems;
    }

    private MessageItem initHeaders() {
        return new MessageItem("Дата",
                "Автор",
                "Сообщение",
                true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MessageItem messageItem = messageItems.get(position);
        holder.item = messageItem;
        holder.dateView.setText(messageItem.date);
        holder.authorView.setText(messageItem.author);
        holder.messageView.setText(messageItem.message);

        holder.view.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(holder.item);
            }
        });
        int color = Color.WHITE;
        if (position == 0) {
            color = Color.GRAY;
        } else if (position % 2 == 0) {
            color = Color.LTGRAY;
        }
        holder.view.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView dateView;
        public final TextView authorView;
        public final TextView messageView;
        public MessageItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            dateView = view.findViewById(R.id.message_date);
            authorView = view.findViewById(R.id.message_author);
            messageView = view.findViewById(R.id.message_text);
        }
    }
}
