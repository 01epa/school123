package com.journal.nn.school123.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.journal.nn.school123.R;
import com.journal.nn.school123.fragment.users.UserItem;
import com.journal.nn.school123.fragment.users.UsersRecyclerViewAdapter;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.pojo.School;

import java.util.ArrayList;
import java.util.List;

import static com.journal.nn.school123.util.LoginUtil.getAllUsers;
import static com.journal.nn.school123.util.LoginUtil.startAddUserActivity;
import static com.journal.nn.school123.util.LoginUtil.startLoadingActivity;

public class UsersActivity extends AppCompatActivity implements UsersRecyclerViewAdapter.ItemClickListener {
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        FloatingActionButton addUserButton = findViewById(R.id.add_user);
        addUserButton.setOnClickListener(view -> startAddUserActivity(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.users_list);
        List<UserItem> userItems = new ArrayList<>();
        getAllUsers(this)
                .forEach(user -> {
                    School school = IntentHelper.getSchools(this)
                            .stream()
                            .filter(s -> s.getId().equals(user.getSchool()))
                            .findAny()
                            .orElse(null);
                    if (school != null) {
                        UserItem userItem = new UserItem(user.getId(),
                                user.getUsername(),
                                school.getCity(),
                                school.getName());
                        userItems.add(userItem);
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(this, userItems);
        usersRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(usersRecyclerViewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Intent intent = getIntent();
        boolean backToUsers = intent.getBooleanExtra(TransferConstants.BACK_TO_USERS, true);
        if (!backToUsers && userItems.size() == 1) {
            onItemClick(recyclerView, 0);
        }
    }

    /**
     * To replace old extras in activity during moving back to users activity
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        UserItem userItem = usersRecyclerViewAdapter.getItem(position);
        startLoadingActivity(this, userItem.userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intentSettings = new Intent(this, Settings.class);
                startActivity(intentSettings);
                return true;
            case R.id.menu_about:
                Intent intentAbout = new Intent(this, About.class);
                startActivity(intentAbout);
                return true;
            case R.id.menu_exit:
                exit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Вы уверены что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    IntentHelper.clear(this);
                    clearNotifications();
                    finishAffinity();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void clearNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }
}
