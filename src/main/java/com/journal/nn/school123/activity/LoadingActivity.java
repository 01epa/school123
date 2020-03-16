package com.journal.nn.school123.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.journal.nn.school123.R;
import com.journal.nn.school123.util.LoginUtil;

import static com.journal.nn.school123.util.UpdateUtil.backToUsersActivity;

public class LoadingActivity extends AppCompatActivity {
    private Button tryAgainButton;
    private Button cancelButton;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent = getIntent();
        userId = intent.getStringExtra(TransferConstants.USER_ID);
        tryAgainButton = findViewById(R.id.try_again);
        tryAgainButton.setOnClickListener(view -> {
            tryAgainButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            showActivity();
        });
        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(view -> {
            tryAgainButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showActivity();
    }

    private void showActivity() {
        LoginUtil.startLogin(this,
                userId,
                getErrorListener());
    }

    private Response.ErrorListener getErrorListener() {
        return error -> {
            tryAgainButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            if (error instanceof TimeoutError
                    || error instanceof NoConnectionError) {
                Toast.makeText(this, getString(R.string.error_server_unavailable), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.refresh_error), Toast.LENGTH_LONG).show();
                System.out.println("Could not login with saved login/password. Error=" + error);
            }
        };
    }

    @Override
    public void onBackPressed() {
        backToUsersActivity(this);
    }
}
