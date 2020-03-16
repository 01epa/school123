package com.journal.nn.school123.fragment.marks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journal.nn.school123.R;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.FinalMark;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Mark;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.pojo.Reason;
import com.journal.nn.school123.pojo.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.journal.nn.school123.util.CurrentPeriodUtil.inPeriod;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MarksRecyclerViewAdapter extends RecyclerView.Adapter<MarksRecyclerViewAdapter.ViewHolder> {
    private static final String MARKS_HEADER = "Оценка";
    private static final int MAX_LINES = 1;
    public static final String YEAR_PERIOD = "360018";
    private final List<MarksItem> marksItems;
    private final MarksFragment.MarksListener mListener;

    public MarksRecyclerViewAdapter(@NonNull MarksFragment.MarksListener listener,
                                    @NonNull Period period,
                                    @NonNull String userId,
                                    @NonNull Context context) {
        Data data = IntentHelper.getData(context, userId);
        if (YEAR_PERIOD.equals(period.getId())) {
            marksItems = initTotalMarksItems(data);
        } else {
            marksItems = initMarksItems(data, period);
        }
        mListener = listener;
    }

    @NonNull
    private ArrayList<MarksItem> initMarksItems(Data data,
                                                Period currentPeriod) {
        ArrayList<MarksItem> marksItems = new ArrayList<>();
        Map<Integer, Subject> studentSubjects = data.getStudentSubjects();
        Map<Integer, List<Mark>> marks = data.getMarks();
        Map<Integer, Reason> reasons = data.getReasons();
        List<FinalMark> finalMarks = data.getFinalMarks();
        marksItems.add(initHeaders(currentPeriod));
        for (Subject subject : studentSubjects.values()) {
            StringBuilder builder = new StringBuilder();
            List<Mark> marksList = marks.getOrDefault(subject.getId(), Collections.emptyList());
            marksList.sort(Comparator.comparing(Mark::getDate));
            String markToSet = "";
            FinalMark finalMark = finalMarks.stream()
                    .filter(mark -> mark.getPeriodId().equals(currentPeriod.getId()))
                    .filter(mark -> mark.getSubjectId() == subject.getId())
                    .findAny()
                    .orElse(null);
            if (finalMark != null) {
                markToSet = finalMark.getMark();
            }
            if (!marksList.isEmpty()) {
                if (finalMark != null) {
                    markToSet += " (";
                }
                double average = marksList.stream()
                        .filter(mark -> inPeriod(mark.getDate(), currentPeriod.getFrom(), currentPeriod.getTo()))
                        .peek(mark -> {
                            Reason reason = reasons.get(mark.getMark());
                            String markString;
                            if (reason == null) {
                                markString = String.valueOf(mark.getMark());
                            } else {
                                markString = reason.getShortReason();
                            }
                            addMark(builder, markString, mark.isJustReceived());
                        })
                        .filter(mark -> !reasons.containsKey(mark.getMark()))
                        .map(Mark::getMark)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0);
                if (average != 0) {
                    markToSet += String.valueOf(Math.round(average * 100.0) / 100.0);
                }
                if (finalMark != null) {
                    markToSet += ")";
                }
            }
            MarksItem marksItem = new MarksItem(subject.getName(),
                    builder.toString(),
                    "",
                    "",
                    "",
                    markToSet,
                    subject,
                    currentPeriod,
                    false);
            marksItems.add(marksItem);
        }
        return marksItems;
    }

    @NonNull
    private ArrayList<MarksItem> initTotalMarksItems(Data data) {
        ArrayList<MarksItem> marksItems = new ArrayList<>();
        Map<Integer, Subject> studentSubjects = data.getStudentSubjects();
        List<Period> periods = data.getClassPeriods()
                .stream()
                .map(classPeriod -> data.getPeriods().get(classPeriod))
                .collect(toMap(Period::getId, period -> period))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(toList());
        if (periods.size() >= 5) {
            marksItems.add(initTotalHeaders(periods));
            List<FinalMark> finalMarks = data.getFinalMarks();
            for (Subject subject : studentSubjects.values()) {
                FinalMark finalMark1 = getFinalMark(finalMarks,
                        subject,
                        periods.get(0));
                FinalMark finalMark2 = getFinalMark(finalMarks,
                        subject,
                        periods.get(1));
                FinalMark finalMark3 = getFinalMark(finalMarks,
                        subject,
                        periods.get(2));
                FinalMark finalMark4 = getFinalMark(finalMarks,
                        subject,
                        periods.get(3));
                FinalMark totalFinalMark = getFinalMark(finalMarks,
                        subject,
                        periods.get(4));
                MarksItem totalMarksItem = new MarksItem(subject.getName(),
                        finalMark1 == null ? "" : finalMark1.getMark(),
                        finalMark2 == null ? "" : finalMark2.getMark(),
                        finalMark3 == null ? "" : finalMark3.getMark(),
                        finalMark4 == null ? "" : finalMark4.getMark(),
                        totalFinalMark == null ? "" : totalFinalMark.getMark(),
                        subject,
                        null,
                        true);
                marksItems.add(totalMarksItem);
            }
        }
        return marksItems;
    }

    private FinalMark getFinalMark(List<FinalMark> finalMarks,
                                   Subject subject,
                                   Period period) {
        return finalMarks.stream()
                .filter(mark -> mark.getPeriodId().equals(period.getId()))
                .filter(mark -> mark.getSubjectId() == subject.getId())
                .findAny()
                .orElse(null);
    }

    private MarksItem initTotalHeaders(List<Period> periods) {
        return new MarksItem("Предмет",
                periods.get(0).getName(),
                periods.get(1).getName(),
                periods.get(2).getName(),
                periods.get(3).getName(),
                periods.get(4).getName(),
                null,
                null,
                true);
    }

    private void addMark(StringBuilder builder,
                         String markString,
                         boolean justReceived) {
        if (justReceived) {
            builder.append("<b>").append(markString).append("</b>");
        } else {
            builder.append(markString);
        }
        builder.append(" ");
    }

    private MarksItem initHeaders(Period currentPeriod) {
        return new MarksItem("Предмет",
                MARKS_HEADER,
                "",
                "",
                "",
                currentPeriod.getName(),
                null,
                null,
                false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_marks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MarksItem marksItem = marksItems.get(position);
        holder.item = marksItem;
        holder.subjectView.setText(marksItem.subjectName);
        holder.period1View.setText(Html.fromHtml(marksItem.marks1String));
        if (holder.item.isHeader()) {
            initTextViewHeader(holder.subjectView);
            initTextViewHeader(holder.period1View);
            initTextViewHeader(holder.period2View);
            initTextViewHeader(holder.period3View);
            initTextViewHeader(holder.period4View);
            initTextViewHeader(holder.markView);
        }
        if (!holder.item.total) {
            ViewGroup.LayoutParams params = holder.period1View.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.period1View.setLayoutParams(params);
        }
        setText(holder.period2View, marksItem.marks2String, holder.item);
        setText(holder.period3View, marksItem.marks3String, holder.item);
        setText(holder.period4View, marksItem.marks4String, holder.item);
        holder.markView.setText(marksItem.mark);
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
        holder.view.invalidate();
    }

    private void initTextViewHeader(TextView textView) {
        textView.setMaxLines(MAX_LINES);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.WHITE);
    }

    private void setText(TextView textView,
                         String text,
                         MarksItem item) {
        if (text == null || text.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            if (item.isHeader()) {
                textView.setMaxLines(MAX_LINES);
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return marksItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView subjectView;
        public final TextView period1View;
        public final TextView period2View;
        public final TextView period3View;
        public final TextView period4View;
        public final TextView markView;
        public MarksItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            subjectView = view.findViewById(R.id.subject);
            period1View = view.findViewById(R.id.period1);
            period2View = view.findViewById(R.id.period2);
            period3View = view.findViewById(R.id.period3);
            period4View = view.findViewById(R.id.period4);
            markView = view.findViewById(R.id.mark);
        }
    }
}
