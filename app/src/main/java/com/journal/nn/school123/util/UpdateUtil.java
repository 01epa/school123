package com.journal.nn.school123.util;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.journal.nn.school123.BuildConfig;
import com.journal.nn.school123.activity.TransferConstants;
import com.journal.nn.school123.activity.UpdateActivity;
import com.journal.nn.school123.activity.UsersActivity;
import com.journal.nn.school123.rest.update.Update;
import com.journal.nn.school123.rest.update.UpdateInfo;

public class UpdateUtil {
    static void checkUpdate(@NonNull Context context,
                            @NonNull Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        Update update = new Update(
                response -> {
                    Version version = gson.fromJson(response, Version.class);
                    if (version.version > BuildConfig.VERSION_CODE) {
                        update(context,
                                requestQueue,
                                gson,
                                version);
                    } else {
                        startUsersActivity(context);
                    }
                },
                errorListener
        );
        requestQueue.add(update);
    }

    private static void update(@NonNull Context context,
                               @NonNull RequestQueue requestQueue,
                               @NonNull Gson gson,
                               @NonNull Version version) {
        UpdateInfo updateInfo = new UpdateInfo(
                response -> {
                    VersionInfo versionInfo = gson.fromJson(response, VersionInfo.class);
                    startUpdateActivity(context, versionInfo);
                },
                error -> {
                    System.out.println("Could not get description for update application");
                    startUsersActivity(context);
                },
                String.valueOf(version.version)
        );
        requestQueue.add(updateInfo);
    }

    private static void startUpdateActivity(@NonNull Context context,
                                            @NonNull VersionInfo versionInfo) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(TransferConstants.DATE, versionInfo.date);
        intent.putExtra(TransferConstants.VERSION, versionInfo.version);
        intent.putExtra(TransferConstants.FILE_NAME, versionInfo.file);
        intent.putExtra(TransferConstants.DESCRIPTION, versionInfo.description);
        intent.putExtra(TransferConstants.FORCE_UPGRADE, versionInfo.forceUpgrade);
        context.startActivity(intent);
    }

    public static void startUsersActivity(@NonNull Context context) {
        Intent intent = new Intent(context, UsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(TransferConstants.BACK_TO_USERS, false);
        context.startActivity(intent);
    }

    public static void backToUsersActivity(@NonNull Context context) {
        Intent intent = new Intent(context, UsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(TransferConstants.BACK_TO_USERS, true);
        context.startActivity(intent);
    }

    private class Version {
        int version;
    }

    private class VersionInfo {
        int version;
        String file;
        String date;
        String description;
        boolean forceUpgrade;
    }
}
