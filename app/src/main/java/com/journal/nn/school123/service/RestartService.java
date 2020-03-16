package com.journal.nn.school123.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartService extends BroadcastReceiver {
    public static final String RESTART_SERVICE_NAME = "com.journal.nn.school123.service.RestartService";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RefreshBackgroundService.class));
    }
}
