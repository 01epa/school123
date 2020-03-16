package com.journal.nn.school123.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.journal.nn.school123.R;
import com.journal.nn.school123.service.RefreshBackgroundService;
import com.journal.nn.school123.util.SchoolUtil;

import static com.journal.nn.school123.util.LoginUtil.moveUser;

public class LaunchActivity extends AppCompatActivity {
    private static final String SHORTCUT = "shortcut";
    private static final String SHORTCUT_CREATED = "created";
    private static final String SHORTCUT_CREATED1 = "created-1";
    private Button tryAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, RefreshBackgroundService.class);
        startService(intent);
        setContentView(R.layout.activity_launch);
        SharedPreferences sharedPreferences = getSharedPreferences(SHORTCUT, Activity.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SHORTCUT_CREATED, false)) {
            removeShortcut();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(SHORTCUT_CREATED);
            edit.apply();
        }
        if (!sharedPreferences.getBoolean(SHORTCUT_CREATED1, false)) {
            addShortcut();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(SHORTCUT_CREATED1, true);
            edit.apply();
        }
        setExceptionHandler();
        tryAgainButton = findViewById(R.id.launch_try_again);
        tryAgainButton.setOnClickListener(view -> {
            tryAgainButton.setVisibility(View.INVISIBLE);
            showActivity();
        });
        moveUser(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showActivity();
    }

    private void setExceptionHandler() {
        Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Exception in " + getString(R.string.app_name), Log.getStackTraceString(paramThrowable));
            clipboard.setPrimaryClip(clip);
            if (oldHandler != null) {
                oldHandler.uncaughtException(paramThread, paramThrowable);
            } else {
                System.exit(2);
            }
        });
    }

    private Response.ErrorListener getErrorListener() {
        return error -> {
            System.out.println("Could not load data. Error=" + error);
            Toast.makeText(this, getString(R.string.error_server_unavailable), Toast.LENGTH_LONG).show();
            tryAgainButton.setVisibility(View.VISIBLE);
        };
    }

    private void showActivity() {
        SchoolUtil.loadSchools(this, getErrorListener());
    }

    private void addShortcut() {
        shortcut("com.android.launcher.action.INSTALL_SHORTCUT",
                getString(R.string.app_name));
    }

    private void removeShortcut() {
        shortcut("com.android.launcher.action.UNINSTALL_SHORTCUT",
                "Школа 123");
    }

    private void shortcut(@NonNull String action,
                          @NonNull String name) {
        Intent shortcutIntent = new Intent(this, LaunchActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
        addIntent.setAction(action);
        addIntent.putExtra("duplicate", false);
        sendBroadcast(addIntent);
    }
}
