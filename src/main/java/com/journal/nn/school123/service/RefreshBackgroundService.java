package com.journal.nn.school123.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.Person;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.journal.nn.school123.R;
import com.journal.nn.school123.activity.LaunchActivity;
import com.journal.nn.school123.activity.MainActivity;
import com.journal.nn.school123.fragment.users.User;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Mark;
import com.journal.nn.school123.pojo.Reason;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.CurrentYear;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RestartService.RESTART_SERVICE_NAME;
import static com.journal.nn.school123.util.LoginUtil.getAddress;
import static com.journal.nn.school123.util.LoginUtil.getAllUsers;
import static com.journal.nn.school123.util.LoginUtil.getCookie;
import static com.journal.nn.school123.util.LoginUtil.getStudentId;
import static com.journal.nn.school123.util.LoginUtil.getUser;
import static com.journal.nn.school123.util.LoginUtil.getVersion;

public class RefreshBackgroundService extends IntentService implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String UPDATE_REQUIRED = "update-ui-required";
    public static final String GROUP_KEY = RefreshBackgroundService.class.getName();
    public static final String CHANNEL_ID = "0";
    public static final int GROUP_ID = 100;
    public static final long REFRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(30);
    private boolean sendNotification = true;
    private AtomicInteger index = new AtomicInteger(0);

    public RefreshBackgroundService() {
        super("refresh in background");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.settings_mark_notification_key);
        sendNotification = sharedPreferences.getBoolean(key, true);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        synchronized (this) {
            try {
                long endTime = System.currentTimeMillis() + REFRESH_TIMEOUT;
                wait(endTime - System.currentTimeMillis());
                loadData();
            } catch (Exception e) {
                System.out.println("Could not interrupt:" + e);
            }
        }
    }

    private void loadData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        getAllUsers(this).forEach(user -> {
            String userId = user.getId();
            String version = getVersion(this, userId);
            Data data = new Data(userId, version);
            RequestParameters requestParameters = new RequestParameters(
                    data,
                    gson,
                    requestQueue,
                    response -> compare(data),
                    error -> System.out.println("Could not get data from service due to=" + error),
                    getCookie(this, userId),
                    getStudentId(this, userId),
                    getAddress(this, userId)
            );
            CurrentYear currentYear = new CurrentYear(requestParameters);
            requestQueue.add(currentYear);
        });
    }

    //private int trd = 0;

    private void compare(Data newData) {
        Data oldData = IntentHelper.getData(this, newData.getId());
        if (oldData == null || oldData.getMarks() == null) {
            IntentHelper.setData(this, newData);
            System.out.println("No data. Set it");
        } else if (newData.getMarks().equals(oldData.getMarks())) {
            System.out.println("No updates for marks");
            /*if (trd < 2) {
                Map<Integer, List<Mark>> marks = oldData.getMarks();
                List<Mark> marks1 = marks.get(1);
                marks1.remove(marks1.size() - 1);
                if (trd % 2 == 0) {
                    marks1.remove(marks1.size() - 1);
                }
                trd++;
            }
            IntentHelper.setData(this, oldData);*/
            IntentHelper.setData(this, newData);
        } else {
            IntentHelper.setData(this, newData);
            System.out.println("Has updates");
            sendNotification(newData);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(RESTART_SERVICE_NAME));
    }

    private void sendNotification(@NonNull Data data) {
        User user = getUser(this, data.getId());
        if (user != null) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            Intent updateIntent = new Intent(UPDATE_REQUIRED);
            updateIntent.putExtra(USER_ID, user.getId());
            localBroadcastManager.sendBroadcast(updateIntent);
            if (sendNotification) {
                long time = new Date().getTime();
                Map<String, StringBuilder> marksToDisplay = new HashMap<>();
                Map<Integer, Reason> reasons = data.getReasons();
                data.getMarks()
                        .forEach((subjectId, marks) -> marks.stream()
                                .filter(Mark::isJustReceived)
                                .map(mark -> {
                                    if (reasons.containsKey(mark.getMark())) {
                                        return reasons.get(mark.getMark()).getShortReason();
                                    } else {
                                        return String.valueOf(mark.getMark());
                                    }
                                })
                                .forEach(mark -> {
                                    String subject = data.getStudentSubjects().get(subjectId).getName();
                                    StringBuilder builder = marksToDisplay.computeIfAbsent(subject, k -> new StringBuilder());
                                    builder.append(mark).append(" ");
                                }));
                Person userPerson = new Person.Builder()
                        .setName(user.getUsername())
                        .build();
                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(userPerson)
                        .setConversationTitle(user.getUsername())
                        .setGroupConversation(true);
                marksToDisplay.forEach((subject, marks) -> {
                    Person subjectPerson = new Person.Builder()
                            .setName(subject)
                            .build();
                    messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(
                            marks.toString(),
                            time,
                            subjectPerson));
                });
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, user.getId());
                PendingIntent pi = PendingIntent.getActivity(this,
                        index.getAndIncrement(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Получены новые оценки")
                        .setStyle(messagingStyle)
                        .setGroup(GROUP_KEY)
                        .setContentIntent(pi)
                        .build();
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                Intent usersIntent = new Intent(this, LaunchActivity.class);
                PendingIntent usersPi = PendingIntent.getActivity(this,
                        index.getAndIncrement(),
                        usersIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setStyle(style)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Получены новые оценки")
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(usersPi);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(user.getId().hashCode(), notification);
                notificationManager.notify(GROUP_ID, builder.build());
            }
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String expectedKey = getString(R.string.settings_mark_notification_key);
        if (key.equals(expectedKey)) {
            sendNotification = sharedPreferences.getBoolean(key, true);
        }
    }
}
