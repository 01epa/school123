package com.journal.nn.school123.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.activity.LoadingActivity;
import com.journal.nn.school123.activity.LoginActivity;
import com.journal.nn.school123.activity.MainActivity;
import com.journal.nn.school123.fragment.users.User;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.School;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.CurrentYear;
import com.journal.nn.school123.rest.login.Auth;
import com.journal.nn.school123.rest.login.Version;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RefreshBackgroundService.REFRESH_TIMEOUT;
import static java.util.stream.Collectors.toList;

public class LoginUtil {
    private static final String COOKIE = "cookie";
    private static final String VERSION = "version";
    private static final String STUDENT_ID = "student-id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SCHOOL = "school";
    private static final String LOGIN_REPOSITORY = "Login";
    private static final String DEFAULT_SCHOOL = "1";

    public static void tryLogin(String userId,
                                String username,
                                String password,
                                String address,
                                Context context,
                                Response.ErrorListener errorListener,
                                Response.Listener<String> successListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        Version version = new Version(userId,
                username,
                password,
                address,
                context,
                errorListener,
                successListener,
                requestQueue,
                gson);
        requestQueue.add(version);
    }


    public static class PlainCookieListener implements Response.Listener<String> {
        @NonNull
        private final String version;
        @NonNull
        private final String userId;
        @NonNull
        private final String username;
        @NonNull
        private final String password;
        @NonNull
        private final String address;
        @NonNull
        private final Context context;
        @NonNull
        private final Response.ErrorListener errorListener;
        @NonNull
        private final Response.Listener<String> successListener;
        @NonNull
        private final RequestQueue requestQueue;
        @NonNull
        private final Gson gson;

        public PlainCookieListener(@NonNull String version,
                                   @NonNull String userId,
                                   @NonNull String username,
                                   @NonNull String password,
                                   @NonNull String address,
                                   @NonNull Context context,
                                   @NonNull Response.ErrorListener errorListener,
                                   @NonNull Response.Listener<String> successListener,
                                   @NonNull RequestQueue requestQueue,
                                   @NonNull Gson gson) {
            this.version = version;
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.address = address;
            this.context = context;
            this.errorListener = errorListener;
            this.successListener = successListener;
            this.requestQueue = requestQueue;
            this.gson = gson;
        }

        @Override
        public void onResponse(String response) {
            JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
            for (JsonElement jsonElement : jsonArray) {
                JsonArray jsonElements = jsonElement.getAsJsonArray();
                if (jsonElements.size() == 9) {
                    String studentId = jsonElements.get(6).getAsString();
                    String schoolUserId = jsonElements.get(0).getAsString();
                    String cookie = getCookie(schoolUserId);
                    Data data = new Data(userId, version);
                    RequestParameters requestParameters = new RequestParameters(
                            data,
                            gson,
                            requestQueue,
                            responseText -> {
                                System.out.println("Login successful.");

                                setVersion(context, userId, version);
                                setUsername(context, userId, username);
                                setPassword(context, userId, password);
                                School school = IntentHelper.getSchools(context)
                                        .stream()
                                        .filter(s -> s.getAddress().equals(address))
                                        .findAny()
                                        .get();
                                setSchool(context, userId, school.getId());
                                set(context, userId, COOKIE, cookie);
                                set(context, userId, STUDENT_ID, studentId);
                                successListener.onResponse(response);
                            },
                            errorListener,
                            cookie,
                            studentId,
                            address
                    );
                    Auth auth = new Auth(requestParameters, schoolUserId);
                    requestQueue.add(auth);
                    return;
                } else {
                    errorListener.onErrorResponse(new VolleyError("No elements in response"));
                }
            }
            errorListener.onErrorResponse(new VolleyError("No data in response"));
        }

        @NonNull
        private String getCookie(String schoolUserId) {
            return "ys-userId=n%3A" + encode(schoolUserId) +
                    "; ys-user=s%3A" + encode(username) +
                    "; ys-password=s%3A" + encode(getPassword(password));
        }

        @NonNull
        protected String getPassword(@NonNull String password) {
            return password;
        }
    }

    public static class Sha1CookieListener extends PlainCookieListener {
        public Sha1CookieListener(@NonNull String version,
                                  @NonNull String userId,
                                  @NonNull String username,
                                  @NonNull String password,
                                  @NonNull String address,
                                  @NonNull Context context,
                                  @NonNull Response.ErrorListener errorListener,
                                  @NonNull Response.Listener<String> successListener,
                                  @NonNull RequestQueue requestQueue,
                                  @NonNull Gson gson) {
            super(version,
                    userId,
                    username,
                    password,
                    address,
                    context,
                    errorListener,
                    successListener,
                    requestQueue,
                    gson);
        }

        @Override
        @NonNull
        protected String getPassword(@NonNull String password) {
            return getSha1Hash(password);
        }
    }

    @NonNull
    public static String getSha1Hash(@NonNull String password) {
        return new String(Hex.encodeHex(DigestUtils.sha(password)));
    }

    private static String encode(String string) {
        string = StringEscapeUtils.escapeJava(string);
        string = string.replaceAll("\\\\", "%");
        return string;
    }

    private static void loadData(@NonNull Context context,
                                 @NonNull String userId,
                                 @NonNull Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        String version = getVersion(context, userId);
        Data data = new Data(userId, version);
        RequestParameters requestParameters = new RequestParameters(
                data,
                gson,
                requestQueue,
                response -> {
                    System.out.println("Data loaded successful");
                    IntentHelper.setData(context, data);
                    startMainActivity(context, userId);
                },
                errorListener,
                getCookie(context, userId),
                getStudentId(context, userId),
                getAddress(context, userId)
        );
        CurrentYear currentYear = new CurrentYear(requestParameters);
        requestQueue.add(currentYear);
    }

    public static void startLogin(@NonNull Context context,
                                  @NonNull String userId,
                                  @NonNull Response.ErrorListener errorListener) {
        String username = getUsername(context, userId);
        String password = getPassword(context, userId);
        String address = getAddress(context, userId);
        tryLogin(userId,
                username,
                password,
                address,
                context,
                errorListener,
                response -> LoginUtil.loadData(context, userId, errorListener));
    }

    public static void startLoadingActivity(@NonNull Context context,
                                            @NonNull String userId) {
        Data data = IntentHelper.getData(context, userId);
        if (data == null || isDataTooOld(data)) {
            Intent intent = new Intent(context, LoadingActivity.class);
            intent.putExtra(USER_ID, userId);
            context.startActivity(intent);
        } else {
            startMainActivity(context, userId);
        }
    }

    private static boolean isDataTooOld(@NonNull Data data) {
        Date date = data.getSaveDate();
        return date == null || date.getTime() + REFRESH_TIMEOUT < new Date().getTime();
    }

    public static void startEditUserActivity(@NonNull Context context,
                                             @NonNull String userId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
    }

    public static void startAddUserActivity(@NonNull Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private static void startMainActivity(@NonNull Context context,
                                          @NonNull String userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
    }

    private static void setUsername(@NonNull Context context,
                                    @NonNull String userId,
                                    @Nullable String username) {
        set(context, userId, USERNAME, username);
    }

    private static String getUsername(@NonNull Context context,
                                      @NonNull String userId) {
        return get(context, userId, USERNAME);
    }

    private static void setPassword(@NonNull Context context,
                                    @NonNull String userId,
                                    @Nullable String password) {
        set(context, userId, PASSWORD, password);
    }

    private static void setVersion(@NonNull Context context,
                                   @NonNull String userId,
                                   @Nullable String version) {
        set(context, userId, VERSION, version);
    }

    private static void setSchool(@NonNull Context context,
                                  @NonNull String userId,
                                  @Nullable String school) {
        set(context, userId, SCHOOL, school);
    }

    private static String getPassword(@NonNull Context context,
                                      @NonNull String userId) {
        return get(context, userId, PASSWORD);
    }

    private static String getSchool(@NonNull Context context,
                                    @NonNull String userId) {
        return get(context, userId, SCHOOL);
    }

    public static String getCookie(@NonNull Context context,
                                   @NonNull String userId) {
        return get(context, userId, COOKIE);
    }

    public static String getStudentId(@NonNull Context context,
                                      @NonNull String userId) {
        return get(context, userId, STUDENT_ID);
    }

    public static String getVersion(@NonNull Context context,
                                    @NonNull String userId) {
        return get(context, userId, VERSION);
    }

    public static String getAddress(@NonNull Context context,
                                    @NonNull String userId) {
        String schoolId = getSchool(context, userId);
        return IntentHelper.getSchools(context)
                .stream()
                .filter(school -> school.getId().equals(schoolId))
                .findAny()
                .get()
                .getAddress();
    }

    private static void set(@NonNull Context context,
                            @NonNull String userId,
                            @NonNull String key,
                            @Nullable String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_REPOSITORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getKey(key, userId), value);
        editor.apply();
    }

    private static String get(@NonNull Context context,
                              @NonNull String userId,
                              @NonNull String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_REPOSITORY, MODE_PRIVATE);
        return sharedPreferences.getString(getKey(key, userId), null);
    }

    public static List<User> getAllUsers(@NonNull Context context) {
        return context.getSharedPreferences(LOGIN_REPOSITORY, MODE_PRIVATE).getAll()
                .keySet()
                .stream()
                .filter(key -> key.startsWith(USERNAME + "."))
                .map(key -> key.split("\\.", 2)[1])
                .map(userId -> getUser(context, userId))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Nullable
    public static User getUser(@NonNull Context context,
                               @NonNull String userId) {
        String userName = getUsername(context, userId);
        String password = getPassword(context, userId);
        String school = getSchool(context, userId);
        if (StringUtils.isAnyEmpty(userName, password, school)) {
            return null;
        }
        return new User(userId,
                userName,
                password,
                school);
    }

    public static void moveUser(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_REPOSITORY, MODE_PRIVATE);
        String userName = sharedPreferences.getString(USERNAME, null);
        String password = sharedPreferences.getString(PASSWORD, null);
        String studentId = sharedPreferences.getString(STUDENT_ID, null);
        String cookie = sharedPreferences.getString(COOKIE, null);
        if (!StringUtils.isAnyEmpty(userName, password, studentId, cookie)) {
            String userId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(USERNAME);
            editor.remove(PASSWORD);
            editor.remove(STUDENT_ID);
            editor.remove(COOKIE);
            editor.apply();
            setUsername(context, userId, userName);
            setPassword(context, userId, password);
            set(context, userId, STUDENT_ID, password);
            set(context, userId, COOKIE, password);
            setSchool(context, userId, DEFAULT_SCHOOL);
        }
    }

    public static void removeUser(@NonNull Context context,
                                  @NonNull String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_REPOSITORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sharedPreferences.getAll()
                .keySet()
                .stream()
                .filter(key -> key.endsWith("." + userId))
                .forEach(editor::remove);
        editor.apply();
    }

    private static String getKey(@NonNull String key,
                                 @NonNull String userId) {
        return key + "." + userId;
    }
}
