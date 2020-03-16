package com.journal.nn.school123.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.users.User;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.School;
import com.journal.nn.school123.util.LoginUtil;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.journal.nn.school123.util.LoginUtil.getUser;
import static com.journal.nn.school123.util.UpdateUtil.backToUsersActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private Spinner citySpinner;
    private Spinner schoolSpinner;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(view -> attemptLogin());
        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(view -> onBackPressed());
        Intent intent = getIntent();
        userId = intent.getStringExtra(TransferConstants.USER_ID);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
        } else {
            loginButton.setText("Изменить");
            setTitle("Изменить ученика");
        }
        User user = getUser(this, userId);
        citySpinner = findViewById(R.id.city);
        schoolSpinner = findViewById(R.id.school);
        Collection<School> schoolsList = IntentHelper.getSchools(this);
        List<String> cities = schoolsList.stream()
                .map(School::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean defaultSet = false;

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                String selectedCity = cities.get(position);
                List<String> schools = schoolsList.stream()
                        .filter(school -> selectedCity.equals(school.getCity()))
                        .map(School::getName)
                        .sorted()
                        .collect(Collectors.toList());
                ArrayAdapter<String> schoolAdapter = new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_spinner_item,
                        schools);
                schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                schoolSpinner.setAdapter(schoolAdapter);
                if (!defaultSet) {
                    defaultSet = true;
                    if (user != null) {
                        setDefaultSpinnerValue(user, schoolSpinner, schools, School::getName);
                    } else {
                        schoolsList.stream()
                                .filter(School::isDefaultValue)
                                .findAny()
                                .ifPresent(school -> setDefaultSpinnerValue(schoolSpinner, schools, School::getName, school));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (user != null) {
            usernameView.setText(user.getUsername());
            passwordView.setText(user.getPassword());
            setDefaultSpinnerValue(user, citySpinner, cities, School::getCity);
        } else {
            schoolsList.stream()
                    .filter(School::isDefaultValue)
                    .findAny()
                    .ifPresent(school -> setDefaultSpinnerValue(citySpinner, cities, School::getCity, school));
        }
    }

    @Override
    protected void onPause() {
        onBackPressed();
        super.onPause();
    }

    private void setDefaultSpinnerValue(@Nullable User user,
                                        @NonNull Spinner spinner,
                                        @NonNull List<String> data,
                                        @NonNull Function<School, String> function) {
        if (user != null) {
            IntentHelper.getSchools(this)
                    .stream()
                    .filter(s -> s.getId().equals(user.getSchool()))
                    .findAny()
                    .ifPresent(school -> setDefaultSpinnerValue(spinner, data, function, school));
        }
    }

    private void setDefaultSpinnerValue(@NonNull Spinner spinner,
                                        @NonNull List<String> data,
                                        @NonNull Function<School, String> function,
                                        @NonNull School school) {
        int index = 0;
        for (; index < data.size(); index++) {
            String key = data.get(index);
            if (key.equals(function.apply(school))) {
                break;
            }
        }
        spinner.setSelection(index);
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_invalid_username));
            usernameView.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            passwordView.requestFocus();
        } else {
            String school = (String) schoolSpinner.getSelectedItem();
            String city = (String) citySpinner.getSelectedItem();
            String address = IntentHelper.getSchools(this)
                    .stream()
                    .filter(s -> s.getCity().equals(city))
                    .filter(s -> s.getName().equals(school))
                    .findAny()
                    .get()
                    .getAddress();
            LoginUtil.tryLogin(userId,
                    username,
                    password,
                    address,
                    this,
                    getErrorListener(),
                    response -> onBackPressed());
        }
    }

    @NonNull
    private Response.ErrorListener getErrorListener() {
        return error -> {
            System.out.println("Could not login. Error=" + error);
            Toast.makeText(this, getString(R.string.error_incorrect_password), Toast.LENGTH_LONG).show();
            usernameView.requestFocus();
        };
    }

    @Override
    public void onBackPressed() {
        backToUsersActivity(this);
    }
}
