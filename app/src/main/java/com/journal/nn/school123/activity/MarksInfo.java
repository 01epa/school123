package com.journal.nn.school123.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.journal.nn.school123.R;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.FinalMark;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Journal;
import com.journal.nn.school123.pojo.Mark;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.pojo.Reason;
import com.journal.nn.school123.pojo.Subject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RefreshBackgroundService.UPDATE_REQUIRED;
import static com.journal.nn.school123.util.CurrentPeriodUtil.inPeriod;

public class MarksInfo extends AppCompatActivity {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_info);

        Intent intent = getIntent();
        int subjectId = intent.getIntExtra(TransferConstants.SUBJECT_ID, 0);
        String periodId = intent.getStringExtra(TransferConstants.PERIOD_ID);
        String userId = intent.getStringExtra(TransferConstants.USER_ID);
        Data data = IntentHelper.getData(this, userId);
        Map<Date, Set<Journal>> periodJournals = data.getPeriodJournals();
        Period period = data.getPeriods().get(periodId);
        Map<Integer, Reason> reasons = data.getReasons();
        List<FinalMark> finalMarks = data.getFinalMarks();
        Subject subject = data.getStudentSubjects().get(subjectId);
        List<Mark> marks = data.getMarks().computeIfAbsent(subjectId, k -> new ArrayList<>());
        TextView teacherView = findViewById(R.id.subject_teacher);
        teacherView.setText(subject.getTeacher());
        TextView titleView = findViewById(R.id.marks_title);
        titleView.setText("Оценки за " + period.getName());

        marks.sort(Comparator.comparing(Mark::getDate));
        String average = "";
        if (!marks.isEmpty()) {
            TableLayout tableData = findViewById(R.id.marks_details);
            TableLayout tableHeader = findViewById(R.id.marks_details_header);
            addRow(tableHeader,
                    Color.GRAY,
                    getString(R.string.mark),
                    getString(R.string.date),
                    getString(R.string.task));
            double av = marks.stream()
                    .filter(mark -> inPeriod(mark.getDate(), period.getFrom(), period.getTo()))
                    .peek(mark -> {
                        String markString;
                        Reason reason = reasons.get(mark.getMark());
                        if (reason == null) {
                            markString = String.valueOf(mark.getMark());
                        } else {
                            markString = reason.getReason();
                        }
                        addRow(tableData,
                                getColor(tableData),
                                markString,
                                DATE_FORMAT.format(mark.getDate()),
                                getDetails(periodJournals, mark, subjectId));
                    })
                    .filter(mark -> !reasons.containsKey(mark.getMark()))
                    .map(Mark::getMark)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
            if (av != 0) {
                average = String.valueOf(Math.round(av * 100.0) / 100.0);
            }
        }
        TextView averageView = findViewById(R.id.subject_average);
        averageView.setText(average);

        FinalMark finalMark = finalMarks.stream()
                .filter(mark -> mark.getPeriodId().equals(periodId))
                .filter(mark -> mark.getSubjectId() == subject.getId())
                .findAny()
                .orElse(null);
        if (finalMark != null) {
            TextView finalMarkView = findViewById(R.id.subject_final_mark);
            finalMarkView.setText(finalMark.getMark());
        }

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(subject.getName());
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receivedUserId = intent.getStringExtra(USER_ID);
                if (Objects.equals(userId, receivedUserId)) {
                    onBackPressed();
                }
            }
        };
    }

    private int getColor(TableLayout table) {
        int childCount = table.getChildCount();
        int color = Color.LTGRAY;
        if (childCount % 2 == 0) {
            color = Color.WHITE;
        }
        return color;
    }

    private void addRow(TableLayout table,
                        int color,
                        String mark,
                        String date,
                        String task) {
        TableRow row = new TableRow(this);
        int childCount = table.getChildCount();
        row.setId(childCount);
        row.setBackgroundColor(color);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        row.addView(createTextView(mark, 40));
        row.addView(createTextView(date, 50));
        row.addView(createTextView(task, 170));

        table.addView(row, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private TextView createTextView(String text,
                                    int size) {
        TextView textView = new TextView(this);
        textView.setTextAppearance(android.R.style.TextAppearance);
        textView.setText(text);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
        textView.setWidth(width);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }

    private String getDetails(Map<Date, Set<Journal>> periodJournals,
                              Mark mark,
                              int subjectId) {
        Set<Journal> journals = periodJournals.get(mark.getDate());
        if (journals == null) {
            return "";
        } else {
            return journals.stream()
                    .filter(journal -> journal.getSubjectId() == subjectId)
                    .map(journal -> journal.getTask() + "\n" + journal.getTaskDetails())
                    .findAny()
                    .orElse("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        onBackPressed();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(UPDATE_REQUIRED));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
        super.onStop();
    }
}
