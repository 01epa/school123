package com.journal.nn.school123.fragment.journal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.SwipeFragment;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Period;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import static com.journal.nn.school123.fragment.marks.MarksRecyclerViewAdapter.YEAR_PERIOD;
import static com.journal.nn.school123.util.CurrentPeriodUtil.getPeriod;

public class JournalFragment extends SwipeFragment {
    private static final DateFormatSymbols SYMBOLS = new DateFormatSymbols();
    private JournalListener mListener;
    private RecyclerView recyclerView;
    private View rootView;
    private int weekDay;
    private Calendar calendar;

    public JournalFragment() {
        super(R.layout.fragment_journal_list,
                R.id.journal_swipe_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = rootView.findViewById(R.id.journal_list);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layout);
        return rootView;
    }

    @Override
    public String getTitle() {
        calendar.set(Calendar.DAY_OF_WEEK, weekDay);
        Date currentTime = calendar.getTime();
        return SYMBOLS.getWeekdays()[weekDay] + "\n"
                + currentTime.getDate() + " " + SYMBOLS.getMonths()[currentTime.getMonth()];
    }

    public void update() {
        Data data = IntentHelper.getData(context, userId);
        Period currentPeriod = getPeriod(data, calendar);
        boolean vacation = currentPeriod == null || YEAR_PERIOD.equals(currentPeriod.getId());
        View vacationView = rootView.findViewById(R.id.vacation_text);
        View contextView = rootView.findViewById(R.id.journal_context);
        if (vacation) {
            vacationView.setVisibility(View.VISIBLE);
            contextView.setVisibility(View.GONE);
        } else {
            vacationView.setVisibility(View.GONE);
            contextView.setVisibility(View.VISIBLE);
            JournalRecyclerViewAdapter adapter = new JournalRecyclerViewAdapter(mListener,
                    weekDay,
                    calendar,
                    userId,
                    context);
            recyclerView.setAdapter(adapter);
            recyclerView.invalidate();
        }
        rootView.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof JournalListener) {
            mListener = (JournalListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + JournalListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setCalendar(@NonNull Calendar calendar) {
        this.calendar = calendar;
    }

    public interface JournalListener {
        void onClick(JournalItem item);
    }
}
