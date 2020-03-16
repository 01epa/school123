package com.journal.nn.school123.fragment.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.SwipeFragment;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;

public class StudentFragment extends SwipeFragment {
    private View rootView;

    public StudentFragment() {
        super(R.layout.fragment_student_list,
                R.id.student_swipe_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    public void update() {
        Data data = IntentHelper.getData(context, userId);
        TextView studentView = rootView.findViewById(R.id.student_student);
        studentView.setText(data.getSecondName() + " " + data.getFirstName() + " " + data.getMiddleName());
        TextView classView = rootView.findViewById(R.id.student_class);
        classView.setText(data.getCurrentClass());
        TextView teacherView = rootView.findViewById(R.id.student_teacher);
        teacherView.setText(data.getClassTeacher());
        TextView schoolView = rootView.findViewById(R.id.student_school);
        schoolView.setText(data.getSchoolDescription());
        TextView versionView = rootView.findViewById(R.id.dnevnik_version);
        versionView.setText(data.getVersion());
    }
}
