package com.journal.nn.school123.fragment.journal;

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
import com.journal.nn.school123.pojo.Journal;
import com.journal.nn.school123.pojo.Mark;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.pojo.Reason;
import com.journal.nn.school123.pojo.Subject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.journal.nn.school123.util.CurrentPeriodUtil.getPeriod;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder> {
    private static final int FOREIGN_LANGUAGE = 4;

    private final List<JournalItem> journalItems;
    private final JournalFragment.JournalListener mListener;
    private String userId;

    public JournalRecyclerViewAdapter(JournalFragment.JournalListener listener,
                                      int day,
                                      @NonNull Calendar calendar,
                                      @NonNull String userId,
                                      @NonNull Context context) {
        this.userId = userId;
        journalItems = initSubjectItems(context, day, calendar);
        mListener = listener;
    }

    @NonNull
    private ArrayList<JournalItem> initSubjectItems(@NonNull Context context,
                                                    int day,
                                                    @NonNull Calendar calendar) {
        ArrayList<JournalItem> journalItems = new ArrayList<>();
        Data data = IntentHelper.getData(context, userId);
        Map<Date, Set<Journal>> journals = data.getJournals();
        Period currentPeriod = getPeriod(data, calendar);
        journalItems.add(initHeaders());
        calendar.set(Calendar.DAY_OF_WEEK, day);
        Date date = calendar.getTime();
        Set<Journal> dateJournals = journals.get(date);
        if (dateJournals == null) {
            JournalItem journalItem = new JournalItem("",
                    "",
                    "Нет данных",
                    "",
                    null,
                    null);
            journalItems.add(journalItem);

        } else {
            int i = 1;
            Map<Integer, Subject> studentSubjects = data.getStudentSubjects();
            Map<Integer, List<Mark>> marks = data.getMarks();
            Map<Integer, Reason> reasons = data.getReasons();
            for (Journal journal : dateJournals) {
                int subjectId = journal.getSubjectId();
                if (subjectId != FOREIGN_LANGUAGE || journal.getGroupNumber().equals(data.getGroupNumber())) {
                    List<Mark> subjectMarks = marks.get(subjectId);
                    StringBuilder builder = new StringBuilder();
                    if (subjectMarks != null) {
                        subjectMarks.stream()
                                .filter(mark -> mark.getDate().equals(date))
                                .forEach(mark -> {
                                    if (reasons.containsKey(mark.getMark())) {
                                        builder.append(reasons.get(mark.getMark()).getShortReason());
                                    } else {
                                        builder.append(mark.getMark());
                                    }
                                    builder.append(" ");
                                });
                    }
                    String task = journal.getTask() + "\n" + journal.getTaskDetails();
                    Subject subject = studentSubjects.get(subjectId);
                    JournalItem journalItem = new JournalItem(String.valueOf(i),
                            subject.getName(),
                            task,
                            builder.toString(),
                            subject,
                            currentPeriod);
                    journalItems.add(journalItem);
                    i++;
                }
            }
        }
        return journalItems;
    }

    private JournalItem initHeaders() {
        return new JournalItem("№",
                "Предмет",
                "Задание",
                "Оценки",
                null,
                null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        JournalItem journalItem = journalItems.get(position);
        holder.item = journalItem;
        holder.numberView.setText(journalItem.number);
        holder.subjectView.setText(journalItem.subjectText);
        holder.taskView.setText(journalItem.task);
        holder.marksView.setText(journalItem.marks);

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
        return journalItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView numberView;
        public final TextView subjectView;
        public final TextView taskView;
        public final TextView marksView;
        public JournalItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            numberView = view.findViewById(R.id.journal_number);
            subjectView = view.findViewById(R.id.journal_subject);
            taskView = view.findViewById(R.id.journal_task);
            marksView = view.findViewById(R.id.journal_marks);
        }
    }
}
