package com.journal.nn.school123.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.journal.nn.school123.R;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.IntentHelper;
import com.journal.nn.school123.rest.RequestParameters;
import com.journal.nn.school123.rest.info.CurrentYear;

import java.util.Objects;

import static com.journal.nn.school123.activity.TransferConstants.USER_ID;
import static com.journal.nn.school123.service.RefreshBackgroundService.UPDATE_REQUIRED;
import static com.journal.nn.school123.util.LoginUtil.getAddress;
import static com.journal.nn.school123.util.LoginUtil.getCookie;
import static com.journal.nn.school123.util.LoginUtil.getStudentId;
import static com.journal.nn.school123.util.LoginUtil.getVersion;

public abstract class SwipeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final int fragment_list;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private final int swipe_container;
    private BroadcastReceiver receiver;
    protected String userId;
    protected Context context;

    public SwipeFragment(int fragment_list,
                         int swipe_container) {
        this.fragment_list = fragment_list;
        this.swipe_container = swipe_container;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(fragment_list, container, false);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receivedUserId = intent.getStringExtra(USER_ID);
                if (Objects.equals(userId, receivedUserId)) {
                    update();
                }
            }
        };
        super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout = rootView.findViewById(swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.post(this::update);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void refresh(Response.Listener<String> successListener) {
        swipeRefreshLayout.setRefreshing(true);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        String version = getVersion(context, userId);
        Data data = new Data(userId, version);
        RequestParameters requestParameters = new RequestParameters(
                data,
                gson,
                requestQueue,
                response -> {
                    swipeRefreshLayout.setRefreshing(false);
                    System.out.println("Data refresh successful");
                    IntentHelper.setData(context, data);
                    successListener.onResponse(response);
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    System.out.println("Could not refresh data. Error=" + error);
                    Toast.makeText(context, getString(R.string.refresh_error), Toast.LENGTH_LONG).show();
                },
                getCookie(context, userId),
                getStudentId(context, userId),
                getAddress(context, userId)
        );
        CurrentYear currentYear = new CurrentYear(requestParameters);
        requestQueue.add(currentYear);
    }

    @Override
    public void onRefresh() {
        refresh(response -> update());
    }

    public abstract void update();

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, new IntentFilter(UPDATE_REQUIRED));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver);
    }

    public String getTitle() {
        return "no name";
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }
}
