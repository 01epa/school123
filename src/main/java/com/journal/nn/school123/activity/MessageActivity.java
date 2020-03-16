package com.journal.nn.school123.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.journal.nn.school123.R;

import java.util.Objects;

import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RefreshBackgroundService.UPDATE_REQUIRED;

public class MessageActivity extends AppCompatActivity {
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        Intent intent = getIntent();
        String author = intent.getStringExtra(TransferConstants.AUTHOR);
        String date = intent.getStringExtra(TransferConstants.DATE);
        String message = intent.getStringExtra(TransferConstants.MESSAGE);
        String userId = intent.getStringExtra(USER_ID);
        TextView authorView = findViewById(R.id.message_info_author);
        authorView.setText(author);
        TextView dateView = findViewById(R.id.message_info_date);
        dateView.setText(date);
        TextView messageView = findViewById(R.id.message_info_text);
        messageView.setText(message);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
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
