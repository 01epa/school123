package com.journal.nn.school123.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.SwipeFragment;
import com.journal.nn.school123.fragment.journal.JournalFragment;
import com.journal.nn.school123.fragment.journal.JournalItem;
import com.journal.nn.school123.fragment.marks.MarksFragment;
import com.journal.nn.school123.fragment.marks.MarksItem;
import com.journal.nn.school123.fragment.messages.MessageItem;
import com.journal.nn.school123.fragment.messages.MessagesFragment;
import com.journal.nn.school123.fragment.student.StudentFragment;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.Journal;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.journal.JournalInfo;
import com.journal.nn.school123.rest.info.journal.JournalInfoListener;
import com.journal.nn.school123.rest.info.journal.PeriodJournalInfo;
import com.journal.nn.school123.rest.info.journal.PeriodJournalInfoListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RefreshBackgroundService.GROUP_ID;
import static com.journal.nn.school123.util.CurrentPeriodUtil.clearCalendar;
import static com.journal.nn.school123.util.CurrentPeriodUtil.getCurrentPeriod;
import static com.journal.nn.school123.util.CurrentPeriodUtil.getPeriod;
import static com.journal.nn.school123.util.LoginUtil.getAddress;
import static com.journal.nn.school123.util.LoginUtil.getCookie;
import static com.journal.nn.school123.util.LoginUtil.getStudentId;
import static com.journal.nn.school123.util.UpdateUtil.backToUsersActivity;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import static java.util.stream.Collectors.toList;

public class MainActivity
        extends AppCompatActivity
        implements MessagesFragment.MessageListener,
        MarksFragment.MarksListener,
        JournalFragment.JournalListener {
    private static final DateFormatSymbols SYMBOLS = new DateFormatSymbols();
    private static final String FRAGMENT_TYPE = "fragmentType";
    private static final int DEFAULT_MENU_ITEM = R.id.marks;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBar;
    private NavigationView navigationView;
    private NavigationView subNavigationView;
    private TabLayout tabLayout;
    private List<SwipeFragment> fragments = new ArrayList<>();
    private SparseArray<String> periodMarks = new SparseArray<>();
    private SparseArray<Long> periodJournals = new SparseArray<>();
    private String userId;
    private int currentView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        userId = intent.getStringExtra(USER_ID);

        drawerLayout = findViewById(R.id.activity_main);
        actionBar = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        TextView userInfoTextView = findViewById(R.id.main_user_info);
        Data data = IntentHelper.getData(this, userId);
        userInfoTextView.setText(data.getSecondName() + " " + data.getFirstName() + ", " + data.getCurrentClass());

        drawerLayout.addDrawerListener(actionBar);
        actionBar.syncState();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        tabLayout = findViewById(R.id.main_tabs);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            processMenuItem(item);
            return true;
        });

        subNavigationView = findViewById(R.id.sub_navigation_view);
        MenuItem menuItem = navigationView.getMenu().findItem(DEFAULT_MENU_ITEM);
        processMenuItem(menuItem);
    }

    /**
     * To replace old extras in activity during invoking from notification
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    private MenuItem getMenuItem() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem;
            }
        }
        return menu.findItem(DEFAULT_MENU_ITEM);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(FRAGMENT_TYPE, getMenuItem().getItemId());
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        int itemId = state.getInt(FRAGMENT_TYPE, DEFAULT_MENU_ITEM);
        MenuItem menuItem = navigationView.getMenu().findItem(itemId);
        processMenuItem(menuItem);
    }

    public void onBackPressed() {
        clearNotifications();
        backToUsersActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_navigate);
        item.setVisible(currentView == R.id.marks || currentView == R.id.journal);
        return true;
    }

    void clearAdapter() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        for (SwipeFragment fragment : fragments) {
            supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        fragments.clear();
    }

    private void processMenuItem(MenuItem item) {
        clearAdapter();
        clearNotifications();
        Menu menu = subNavigationView.getMenu();
        menu.clear();
        int currentItem = 0;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        switch (item.getItemId()) {
            case R.id.marks:
                initMarkFragment(menu);
                break;
            case R.id.journal:
                Calendar currentCalendar = Calendar.getInstance();
                clearCalendar(currentCalendar);
                long time = currentCalendar.getTimeInMillis();
                initJournalFragment(time);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                for (int i = 0; i < fragments.size(); i++) {
                    JournalFragment fragment = (JournalFragment) fragments.get(i);
                    if (fragment.getWeekDay() == day) {
                        currentItem = i;
                        break;
                    }
                }
                break;
            case R.id.messages:
                fragments.add(createMessagesFragment());
                break;
            case R.id.student:
                fragments.add(createStudentFragment());
                break;
        }

        initFragments(item, currentItem);
        setTitle(item.getTitle());
        currentView = item.getItemId();
        invalidateOptionsMenu();
    }

    @NonNull
    private MessagesFragment createMessagesFragment() {
        MessagesFragment fragment = new MessagesFragment();
        fragment.setUserId(userId);
        return fragment;
    }

    @NonNull
    private StudentFragment createStudentFragment() {
        StudentFragment fragment = new StudentFragment();
        fragment.setUserId(userId);
        return fragment;
    }

    private void initMarkFragment(@NonNull Menu menu) {
        Data data = IntentHelper.getData(this, userId);
        Period currentPeriod = getCurrentPeriod(data);
        createMarksFragment(currentPeriod);

        List<String> classPeriods = data.getClassPeriods();
        Map<String, Period> periods = data.getPeriods();
        int itemId = 0;
        periodMarks.clear();
        for (String periodId : classPeriods) {
            Period period = periods.get(periodId);
            periodMarks.put(itemId, periodId);
            MenuItem menuItem = menu.add(Menu.NONE, itemId++, Menu.NONE, period.getName());
            if (currentPeriod.equals(period)) {
                menuItem.setChecked(true);
            }
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        subNavigationView.setNavigationItemSelectedListener(subItem -> {
            selectItem(menu, subItem);
            processMarkMenuItem(subItem);
            return true;
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
    }

    private void createMarksFragment(@NonNull Period currentPeriod) {
        MarksFragment fragment = new MarksFragment();
        fragment.setPeriod(currentPeriod);
        fragment.setUserId(userId);
        fragments.add(fragment);
    }

    private void selectItem(@NonNull Menu menu,
                            @NonNull MenuItem selectedMenuItem) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }
        selectedMenuItem.setChecked(true);
    }

    private void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        List<StatusBarNotification> notifications = Arrays.asList(notificationManager.getActiveNotifications());
        String groupKey = notifications.stream()
                .filter(notification -> notification.getId() == GROUP_ID)
                .map(StatusBarNotification::getGroupKey)
                .findAny()
                .orElse(null);
        List<Integer> notificationIds = notifications.stream()
                .filter(notification -> notification.getGroupKey().equals(groupKey))
                .map(StatusBarNotification::getId)
                .collect(toList());
        int notificationId = userId.hashCode();
        if (notificationIds.size() == 2 && notificationIds.contains(notificationId)) {
            notificationManager.cancel(GROUP_ID);
        } else {
            notificationManager.cancel(notificationId);
        }
    }

    private void initJournalFragment(long time) {
        Menu menu = subNavigationView.getMenu();
        menu.clear();
        createJournalFragments(time);
        int size = 2;
        periodJournals.clear();
        int itemId = 0;
        Data data = IntentHelper.getData(this, userId);
        for (int i = -size; i <= size; i++) {
            String period = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.WEEK_OF_YEAR, i);
            calendar.set(Calendar.DAY_OF_WEEK, MONDAY);
            clearCalendar(calendar);
            Date startDate = calendar.getTime();
            Period expectedPeriod = getPeriod(data, calendar);
            if (expectedPeriod != null) {
                if (i % size == 0 && i != 0) {
                    if (i > 0) {
                        period = "Еще позже";
                    } else {
                        period = "Еще раньше";
                    }
                } else {
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    Date endDate = calendar.getTime();
                    period = startDate.getDate() + " "
                            + SYMBOLS.getMonths()[startDate.getMonth()] + " - "
                            + endDate.getDate() + " "
                            + SYMBOLS.getMonths()[endDate.getMonth()];
                }
                periodJournals.put(itemId, startDate.getTime());
                MenuItem menuItem = menu.add(Menu.NONE, itemId++, Menu.NONE, period);
                if (i == 0) {
                    menuItem.setChecked(true);
                }
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        subNavigationView.setNavigationItemSelectedListener(subItem -> {
            selectItem(menu, subItem);
            processJournalMenuItem(subItem);
            return true;
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
    }

    private void processJournalMenuItem(@NonNull MenuItem item) {
        Data data = IntentHelper.getData(this, userId);
        Long time = periodJournals.get(item.getItemId());
        if (data.isJournalLoaded(time)) {
            initJournalFragment(item, time);
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Gson gson = new Gson();
            RequestParameters requestParameters = new RequestParameters(
                    data,
                    gson,
                    requestQueue,
                    response -> {
                        IntentHelper.setData(this, data);
                        initJournalFragment(item, time);
                    },
                    error -> {
                        System.out.println("Could not get partial journal info due to=" + error);
                        initJournalFragment(item, time);
                    },
                    getCookie(this, userId),
                    getStudentId(this, userId),
                    getAddress(this, userId)
            );
            JournalInfoListener listener = new JournalInfoListener(requestParameters) {
                @Override
                protected void proceedResult(@NonNull Map<Date, Set<Journal>> journals) {
                    requestParameters.getData().addJournals(journals);
                }

                @Override
                protected void nextStep(String response) {
                    Period currentPeriod = getCurrentPeriod(data);
                    Period expectedPeriod = getPeriod(data, calendar);
                    if (currentPeriod.equals(expectedPeriod) || data.isPeriodLoaded(expectedPeriod.getId())) {
                        requestParameters.getSuccessListener().onResponse(response);
                    } else {
                        loadPeriodJournalInfo(expectedPeriod, requestParameters);
                    }
                }
            };
            JournalInfo periodJournalInfo = new JournalInfo(requestParameters, listener) {
                @Override
                protected Calendar getCalendar() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    return calendar;
                }
            };
            requestQueue.add(periodJournalInfo);
        }
    }

    private void initJournalFragment(@NonNull MenuItem item,
                                     long time) {
        clearAdapter();
        int currentItem = 0;
        initJournalFragment(time);
        initFragments(item, currentItem);
    }

    private void createJournalFragments(long time) {
        addJournalFragment(time, MONDAY);
        addJournalFragment(time, TUESDAY);
        addJournalFragment(time, WEDNESDAY);
        addJournalFragment(time, THURSDAY);
        addJournalFragment(time, FRIDAY);
        addJournalFragment(time, SATURDAY);
        addJournalFragment(time, SUNDAY);
    }

    private void initFragments(@NonNull MenuItem item,
                               int currentItem) {
        ViewPager viewPager = findViewById(R.id.mainActivityContent);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        if (fragments.size() > 1) {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(currentItem);
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        item.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void processMarkMenuItem(@NonNull MenuItem item) {
        Data data = IntentHelper.getData(this, userId);
        String periodId = periodMarks.get(item.getItemId());
        Map<String, Period> periods = data.getPeriods();
        Period period = periods.get(periodId);
        if (data.isPeriodLoaded(periodId)) {
            initMarksFragment(item, period);
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Gson gson = new Gson();
            RequestParameters requestParameters = new RequestParameters(
                    data,
                    gson,
                    requestQueue,
                    response -> {
                        IntentHelper.setData(this, data);
                        initMarksFragment(item, period);
                    },
                    error -> {
                        System.out.println("Could not get partial period journal info due to=" + error);
                        initMarksFragment(item, period);
                    },
                    getCookie(this, userId),
                    getStudentId(this, userId),
                    getAddress(this, userId)
            );
            loadPeriodJournalInfo(period, requestParameters);
        }
    }

    private void loadPeriodJournalInfo(@NonNull Period period,
                                       @NonNull RequestParameters requestParameters) {
        PeriodJournalInfoListener listener = new PeriodJournalInfoListener(requestParameters) {
            @Override
            protected void proceedResult(@NonNull Map<Date, Set<Journal>> journals) {
                requestParameters.getData().addPeriodJournals(journals);
            }
        };
        PeriodJournalInfo partialPeriodJournalInfo = new PeriodJournalInfo(requestParameters, listener) {
            @Override
            protected Period getPeriod() {
                return period;
            }
        };
        requestParameters.getRequestQueue().add(partialPeriodJournalInfo);
    }

    private void initMarksFragment(@NonNull MenuItem item,
                                   @NonNull Period period) {
        clearAdapter();
        int currentItem = 0;
        createMarksFragment(period);
        initFragments(item, currentItem);
    }

    private void addJournalFragment(long time,
                                    int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        JournalFragment fragment = new JournalFragment();
        fragment.setWeekDay(day);
        fragment.setCalendar(calendar);
        fragment.setUserId(userId);
        fragments.add(fragment);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBar.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBar.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                for (int index = 0; index < fragments.size(); index++) {
                    SwipeFragment fragment = fragments.get(0);
                    fragment.refresh(response -> {
                        if (fragments.size() > 1) {
                            for (int i = 1; i < fragments.size(); i++) {
                                fragments.get(i).update();
                            }
                        }
                    });
                }
                return true;
            case R.id.menu_change_user:
                onBackPressed();
                return true;
            case R.id.menu_navigate:
                if (drawerLayout.isDrawerOpen(Gravity.END)) {
                    drawerLayout.closeDrawers();
                } else if (drawerLayout.getDrawerLockMode(Gravity.END) != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
                    drawerLayout.openDrawer(Gravity.END, true);
                }
                return true;
        }
        if (actionBar.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Clear new marks once main activity opened
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            IntentHelper.clearData(this, userId);
        }
    }

    @Override
    protected void onPause() {
        if (getParent() instanceof LoadingActivity) {
            onBackPressed();
        }
        super.onPause();
    }

    @Override
    public void onClick(MarksItem item) {
        if (item.isClickable()) {
            Intent intent = new Intent(this, MarksInfo.class);
            intent.putExtra(TransferConstants.SUBJECT_ID, item.subject.getId());
            intent.putExtra(TransferConstants.PERIOD_ID, item.period.getId());
            intent.putExtra(USER_ID, userId);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(JournalItem item) {
        if (!item.isHeader()) {
            Intent intent = new Intent(this, MarksInfo.class);
            intent.putExtra(TransferConstants.SUBJECT_ID, item.subject.getId());
            intent.putExtra(TransferConstants.PERIOD_ID, item.period.getId());
            intent.putExtra(USER_ID, userId);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(MessageItem item) {
        if (!item.isHeader()) {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra(TransferConstants.AUTHOR, item.author);
            intent.putExtra(TransferConstants.DATE, item.date);
            intent.putExtra(TransferConstants.MESSAGE, item.message);
            intent.putExtra(USER_ID, userId);
            startActivity(intent);
        }
    }
}